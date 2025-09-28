package jlu.kemiko.libman.common.validation

/**
 * Reusable validation helpers for critical identifiers.
 */
object Validators {
    private val studentIdRegex = Regex("^\\d{8}$")
    private val isbn13Regex = Regex("^\\d{13}$")

    fun isValidStudentId(studentId: String): Boolean = studentIdRegex.matches(studentId)

    fun isValidIsbn13(isbn13: String): Boolean {
        if (!isbn13Regex.matches(isbn13)) return false
        val digits = isbn13.map { it - '0' }
        val expectedChecksum = (10 - (digits.take(12).withIndex().sumOf { (index, digit) ->
            if (index % 2 == 0) digit else digit * 3
        } % 10)) % 10
        return expectedChecksum == digits.last()
    }
}
