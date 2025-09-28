package jlu.kemiko.libman.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jlu.kemiko.libman.data.local.dao.BookDao
import jlu.kemiko.libman.data.local.dao.LoanDao
import jlu.kemiko.libman.data.local.dao.PatronDao
import jlu.kemiko.libman.data.local.entity.BookEntity
import jlu.kemiko.libman.data.local.entity.LoanEntity
import jlu.kemiko.libman.data.local.entity.PatronEntity

@Database(
    entities = [BookEntity::class, LoanEntity::class, PatronEntity::class],
    version = LibmanDatabaseInfo.VERSION,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class LibmanDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun loanDao(): LoanDao
    abstract fun patronDao(): PatronDao

    companion object {
        const val NAME: String = "libman.db"
    }
}

object LibmanDatabaseInfo {
    const val VERSION = 1
}
