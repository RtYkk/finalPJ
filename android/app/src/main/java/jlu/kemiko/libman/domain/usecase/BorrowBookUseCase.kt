package jlu.kemiko.libman.domain.usecase

import jlu.kemiko.libman.common.validation.Validators
import jlu.kemiko.libman.domain.model.BorrowBookResult
import jlu.kemiko.libman.data.repository.InventoryRepository

/**
 * Validates inputs, ensures preconditions, and delegates to the repository for transactional work.
 */
class BorrowBookUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(isbn13: String, studentId: String): BorrowBookResult {
        if (!Validators.isValidIsbn13(isbn13)) {
            return BorrowBookResult.InvalidIsbn
        }
        if (!Validators.isValidStudentId(studentId)) {
            return BorrowBookResult.InvalidStudentId
        }

        val book = inventoryRepository.fetchBookByIsbn(isbn13)
            ?: return BorrowBookResult.BookNotCataloged
        if (book.availableCount <= 0) {
            return BorrowBookResult.NoCopiesAvailable
        }

        val patron = inventoryRepository.fetchPatron(studentId)
            ?: return BorrowBookResult.PatronNotRegistered

        val borrowResult = inventoryRepository.borrowBook(isbn13 = isbn13, studentId = studentId)
        if (borrowResult.isFailure) {
            val cause = borrowResult.exceptionOrNull()
            return BorrowBookResult.Failure(
                message = cause?.message ?: "Unable to borrow the selected book.",
                cause = cause
            )
        }

        val updatedBook = inventoryRepository.fetchBookByIsbn(isbn13)
        val remainingCopies = updatedBook?.availableCount ?: (book.availableCount - 1)

        return BorrowBookResult.Success(
            isbn13 = isbn13,
            studentId = studentId,
            patronName = patron.name,
            remainingCopies = remainingCopies
        )
    }
}
