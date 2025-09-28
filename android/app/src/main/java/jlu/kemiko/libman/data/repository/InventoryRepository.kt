package jlu.kemiko.libman.data.repository

import jlu.kemiko.libman.data.model.Book
import jlu.kemiko.libman.data.model.Loan
import jlu.kemiko.libman.data.model.Patron
import kotlinx.coroutines.flow.Flow

/**
 * Contract for reading and mutating the local Room inventory tables.
 */
interface InventoryRepository {
    fun observeBooks(): Flow<List<Book>>

    suspend fun fetchBookByIsbn(isbn13: String): Book?

    suspend fun upsertBooks(vararg books: Book)

    suspend fun recordLoans(vararg loans: Loan)

    suspend fun upsertPatrons(vararg patrons: Patron)

    suspend fun fetchPatron(studentId: String): Patron?

    suspend fun borrowBook(isbn13: String, studentId: String): Result<Unit>

    suspend fun returnBook(loanId: Long): Result<Unit>
}
