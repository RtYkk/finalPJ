package jlu.kemiko.libman.data.local.db

import android.content.Context
import androidx.room.Room

/**
 * Builds the singleton Room database instance.
 */
object LibmanDatabaseFactory {
    fun create(context: Context): LibmanDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            LibmanDatabase::class.java,
            LibmanDatabase.NAME
        )
            .addMigrations(*LibmanDatabaseMigrations.ALL)
            .build()
}
