package jlu.kemiko.libman.ui

/**
 * Represents the authenticated context for the current user.
 */
sealed class AuthSession {
    data object Admin : AuthSession()
    data class Student(val studentId: String) : AuthSession()
}
