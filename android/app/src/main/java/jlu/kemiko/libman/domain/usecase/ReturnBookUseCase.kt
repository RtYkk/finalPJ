package jlu.kemiko.libman.domain.usecase

import jlu.kemiko.libman.data.repository.InventoryRepository

/**
 * Validates and performs the return flow while keeping available counts in range.
 */
class ReturnBookUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(loanId: Long): Result<Unit> {
        // TODO: Implement validation and transactional return logic
        return inventoryRepository.returnBook(loanId)
    }
}
