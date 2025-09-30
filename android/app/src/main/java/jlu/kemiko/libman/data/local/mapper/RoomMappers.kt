package jlu.kemiko.libman.data.local.mapper

import jlu.kemiko.libman.data.local.entity.BookEntity
import jlu.kemiko.libman.data.local.entity.LoanEntity
import jlu.kemiko.libman.data.local.entity.PatronEntity
import jlu.kemiko.libman.data.model.Book
import jlu.kemiko.libman.data.model.Loan
import jlu.kemiko.libman.data.model.Patron

fun BookEntity.toModel(): Book = Book(
    isbn13 = isbn13,
    title = title,
    author = author,
    description = description,
    coverImageUrl = coverImageUrl,
    copyCount = copyCount,
    availableCount = availableCount,
    metadataFetchedAt = metadataFetchedAt
)

fun Book.toEntity(): BookEntity = BookEntity(
    isbn13 = isbn13,
    title = title,
    author = author,
    description = description,
    coverImageUrl = coverImageUrl,
    copyCount = copyCount,
    availableCount = availableCount,
    metadataFetchedAt = metadataFetchedAt
)

fun LoanEntity.toModel(): Loan = Loan(
    loanId = loanId,
    isbn13 = isbn13,
    studentId = studentId,
    status = status,
    borrowedAt = borrowedAt,
    dueAt = dueAt,
    returnedAt = returnedAt
)

fun Loan.toEntity(): LoanEntity = LoanEntity(
    loanId = loanId,
    isbn13 = isbn13,
    studentId = studentId,
    status = status,
    borrowedAt = borrowedAt,
    dueAt = dueAt,
    returnedAt = returnedAt
)

fun PatronEntity.toModel(): Patron = Patron(
    studentId = studentId,
    name = name,
    joinedAt = joinedAt
)

fun Patron.toEntity(): PatronEntity = PatronEntity(
    studentId = studentId,
    name = name,
    joinedAt = joinedAt
)
