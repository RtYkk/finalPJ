package jlu.kemiko.libman.network

/**
 * Placeholder for the Retrofit service that fetches metadata for a given ISBN-13.
 */
interface IsbnLookupService {
    suspend fun fetchMetadata(isbn13: String): Result<IsbnMetadataDto>
}

/**
 * Minimal DTO capturing the metadata fields persisted locally after lookup.
 */
data class IsbnMetadataDto(
    val isbn13: String,
    val title: String?,
    val author: String?,
    val publisher: String?,
    val publishedDate: String?,
    val coverImageUrl: String?
)
