package jlu.kemiko.libman.data.repository

import androidx.room.withTransaction
import jlu.kemiko.libman.common.validation.Validators
import jlu.kemiko.libman.data.local.dao.BookDao
import jlu.kemiko.libman.data.local.dao.LoanDao
import jlu.kemiko.libman.data.local.dao.PatronDao
import jlu.kemiko.libman.data.local.db.LibmanDatabase
import jlu.kemiko.libman.data.local.entity.LoanEntity
import jlu.kemiko.libman.data.local.mapper.toEntity
import jlu.kemiko.libman.data.local.mapper.toModel
import jlu.kemiko.libman.data.model.Book
import jlu.kemiko.libman.data.model.Loan
import jlu.kemiko.libman.data.model.LoanStatus
import jlu.kemiko.libman.data.model.Patron
import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomInventoryRepository(
    private val database: LibmanDatabase,
    private val bookDao: BookDao = database.bookDao(),
    private val loanDao: LoanDao = database.loanDao(),
    private val patronDao: PatronDao = database.patronDao(),
    private val clock: Clock = Clock.systemUTC()
) : InventoryRepository {

    override fun observeBooks(): Flow<List<Book>> =
        bookDao.observeBooks().map { entities -> entities.map { it.toModel() } }

    override suspend fun fetchBookByIsbn(isbn13: String): Book? =
        bookDao.getByIsbn(isbn13)?.toModel()

    override suspend fun upsertBooks(vararg books: Book) {
        if (books.isEmpty()) return
        database.withTransaction {
            books.forEach { book ->
                validateIsbn13(book.isbn13)
                require(book.copyCount >= 0) { "copyCount must not be negative" }
                require(book.availableCount in 0..book.copyCount) {
                    "availableCount must be within [0, copyCount]"
                }
            }
            bookDao.upsertAll(books.map(Book::toEntity))
        }
    }

    override suspend fun recordLoans(vararg loans: Loan) {
        if (loans.isEmpty()) return
        database.withTransaction {
            val entities = loans.map { loan ->
                validateIsbn13(loan.isbn13)
                validateStudentId(loan.studentId)
                loan.toEntity()
            }
            loanDao.upsertAll(entities)
        }
    }

    override suspend fun fetchLoan(loanId: Long): Loan? = loanDao.getById(loanId)?.toModel()

    override suspend fun upsertPatrons(vararg patrons: Patron) {
        if (patrons.isEmpty()) return
        database.withTransaction {
            patrons.forEach { patron ->
                validateStudentId(patron.studentId)
                require(patron.name.isNotBlank()) { "Patron name must not be blank" }
            }
            patronDao.upsertAll(patrons.map(Patron::toEntity))
        }
    }

    override suspend fun fetchPatron(studentId: String): Patron? {
        validateStudentId(studentId)
        return patronDao.getByStudentId(studentId)?.toModel()
    }

    override suspend fun borrowBook(isbn13: String, studentId: String): Result<Unit> =
        runCatching {
            validateIsbn13(isbn13)
            validateStudentId(studentId)
            database.withTransaction {
                val book = bookDao.getByIsbn(isbn13)
                    ?: error("No book with ISBN-13 $isbn13 is cataloged")
                require(book.availableCount > 0) { "No available copies to borrow" }
                val patron = patronDao.getByStudentId(studentId)
                    ?: error("Student $studentId is not registered as a patron")
                val updatedAvailable = book.availableCount - 1
                bookDao.updateAvailableCount(isbn13, updatedAvailable)
                val loanEntity = LoanEntity(
                    loanId = 0,
                    isbn13 = isbn13,
                    studentId = patron.studentId,
                    status = LoanStatus.BORROWED,
                    borrowedAt = Instant.now(clock),
                    dueAt = null,
                    returnedAt = null
                )
                loanDao.upsert(loanEntity)
            }
        }

    override suspend fun returnBook(loanId: Long): Result<Unit> = runCatching {
        database.withTransaction {
            val loan = loanDao.getById(loanId) ?: error("Loan $loanId was not found")
            if (loan.status == LoanStatus.RETURNED) {
                return@withTransaction
            }
            val book = bookDao.getByIsbn(loan.isbn13)
                ?: error("Book ${loan.isbn13} referenced by loan $loanId is missing")
            val newAvailable = (book.availableCount + 1).also {
                require(it <= book.copyCount) { "availableCount cannot exceed copyCount" }
            }
            bookDao.updateAvailableCount(book.isbn13, newAvailable)
            loanDao.markReturned(
                loanId = loanId,
                status = LoanStatus.RETURNED,
                returnedAt = Instant.now(clock)
            )
        }
    }

    private fun validateIsbn13(isbn13: String) {
        require(Validators.isValidIsbn13(isbn13)) { "ISBN-13 must be 13 digits with a valid checksum" }
    }

    private fun validateStudentId(studentId: String) {
        require(Validators.isValidStudentId(studentId)) { "StudentId must match ^\\d{8}$" }
    }
}
