package jlu.kemiko.libman.domain.usecase

import jlu.kemiko.libman.data.repository.InventoryRepository

/**
 * Validates and performs the borrow flow inside a Room transaction.
 */
class BorrowBookUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(isbn13: String, studentId: String): Result<Unit> {
        // TODO: Implement validation and transactional borrow logic
        return inventoryRepository.borrowBook(isbn13 = isbn13, studentId = studentId)
    }
}
