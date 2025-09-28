package jlu.kemiko.libman.data.model

/**
 * Represents a student or administrator allowed to borrow books.
 */
data class Patron(
    val studentId: String,
    val name: String,
    val joinedAt: Long
)
