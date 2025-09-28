package jlu.kemiko.libman.domain.model

/**
 * Outcome of attempting to borrow a book.
 */
sealed interface BorrowBookResult {
    data class Success(
        val isbn13: String,
        val studentId: String,
        val patronName: String,
        val remainingCopies: Int
    ) : BorrowBookResult

    data object InvalidIsbn : BorrowBookResult

    data object InvalidStudentId : BorrowBookResult

    data object BookNotCataloged : BorrowBookResult

    data object NoCopiesAvailable : BorrowBookResult

    data object PatronNotRegistered : BorrowBookResult

    data class Failure(val message: String, val cause: Throwable? = null) : BorrowBookResult
}
