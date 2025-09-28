package jlu.kemiko.libman.data.model

import java.time.Instant

/**
 * Tracks the lifecycle of a borrowed book copy.
 */
data class Loan(
    val loanId: Long,
    val isbn13: String,
    val studentId: String,
    val status: LoanStatus,
    val borrowedAt: Instant,
    val dueAt: Instant?,
    val returnedAt: Instant?
)

enum class LoanStatus {
    BORROWED,
    RETURNED,
    OVERDUE
}
