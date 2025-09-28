package jlu.kemiko.libman.common.di

import android.content.Context
import jlu.kemiko.libman.data.local.db.LibmanDatabaseFactory
import jlu.kemiko.libman.data.repository.InventoryRepository
import jlu.kemiko.libman.data.repository.RoomInventoryRepository

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
class DefaultAppContainer(context: Context) : AppContainer {
    private val database = LibmanDatabaseFactory.create(context)

    override val inventoryRepository: InventoryRepository by lazy {
        RoomInventoryRepository(database)
    }
}
