package jlu.kemiko.libman.data.model

/**
 * Represents a cataloged book copy tracked by the offline Room database.
 */
data class Book(
    val isbn13: String,
    val title: String,
    val author: String?,
    val description: String?,
    val coverImageUrl: String?,
    val copyCount: Int,
    val availableCount: Int,
    val metadataFetchedAt: Long?
)
