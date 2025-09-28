package jlu.kemiko.libman.domain.model

import jlu.kemiko.libman.data.model.LoanStatus

/**
 * Outcome of attempting to return a borrowed book copy.
 */
sealed interface ReturnBookResult {
    data class Success(
        val loanId: Long,
        val isbn13: String,
        val status: LoanStatus,
        val availableCopies: Int
    ) : ReturnBookResult

    data object InvalidLoanId : ReturnBookResult

    data object LoanNotFound : ReturnBookResult

    data object AlreadyReturned : ReturnBookResult

    data object BookNotCataloged : ReturnBookResult

    data class Failure(val message: String, val cause: Throwable? = null) : ReturnBookResult
}
