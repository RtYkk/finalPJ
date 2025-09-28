package jlu.kemiko.libman.common.di

import jlu.kemiko.libman.data.repository.InventoryRepository

/**
 * Simple service locator used until a full dependency injection framework is introduced.
 */
interface AppContainer {
    val inventoryRepository: InventoryRepository
}

/**
 * Default implementation wiring repositories and data sources.
 * TODO: Replace with Hilt or another DI solution when feature work begins.
 */
class DefaultAppContainer : AppContainer {
    override val inventoryRepository: InventoryRepository =
        TODO("Provide a concrete repository implementation")
}
