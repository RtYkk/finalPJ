package jlu.kemiko.libman.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["title"]),
        Index(value = ["author"])
    ]
)
data class BookEntity(
    @PrimaryKey
    val isbn13: String,
    val title: String,
    val author: String?,
    val description: String?,
    @ColumnInfo(name = "cover_image_url")
    val coverImageUrl: String?,
    @ColumnInfo(name = "copy_count")
    val copyCount: Int,
    @ColumnInfo(name = "available_count")
    val availableCount: Int,
    @ColumnInfo(name = "metadata_fetched_at")
    val metadataFetchedAt: Long?
)
