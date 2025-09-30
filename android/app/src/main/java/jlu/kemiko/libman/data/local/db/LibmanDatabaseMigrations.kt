package jlu.kemiko.libman.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Central place to define Room migrations.
 */
object LibmanDatabaseMigrations {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE books ADD COLUMN description TEXT")
            database.execSQL("ALTER TABLE books ADD COLUMN cover_image_url TEXT")
        }
    }

    val ALL: Array<Migration> = arrayOf(
        MIGRATION_1_2
    )
}
