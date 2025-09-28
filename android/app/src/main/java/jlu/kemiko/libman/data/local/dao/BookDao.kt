package jlu.kemiko.libman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jlu.kemiko.libman.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isbn13 = :isbn13 LIMIT 1")
    suspend fun getByIsbn(isbn13: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(books: List<BookEntity>)

    @Query("UPDATE books SET available_count = :availableCount WHERE isbn13 = :isbn13")
    suspend fun updateAvailableCount(isbn13: String, availableCount: Int)
}
