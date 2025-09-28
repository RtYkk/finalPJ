package jlu.kemiko.libman.common.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun studentIdMustBeEightDigits() {
        assertTrue(Validators.isValidStudentId("12345678"))
        assertFalse(Validators.isValidStudentId("1234567"))
        assertFalse(Validators.isValidStudentId("123456789"))
        assertFalse(Validators.isValidStudentId("abcd5678"))
    }

    @Test
    fun isbn13ChecksumIsValidated() {
        assertTrue(Validators.isValidIsbn13("9780306406157"))
        assertFalse(Validators.isValidIsbn13("9780306406158"))
        assertFalse(Validators.isValidIsbn13("978030640615"))
        assertFalse(Validators.isValidIsbn13("abc0306406157"))
    }
}
