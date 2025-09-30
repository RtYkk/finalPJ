package jlu.kemiko.libman.network

/**
 * Placeholder for the Retrofit service that fetches metadata for a given ISBN-13.
 */
interface IsbnLookupService {
    suspend fun fetchMetadata(isbn13: String): Result<IsbnMetadataDto>
}

/**
 * Basic metadata fields returned by each provider.
 */
data class IsbnMetadataDto(
    val isbn13: String,
    val title: String?,
    val author: String?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val coverImageUrl: String?
)

/**
 * Aggregated metadata from multiple services.
 */
data class CombinedIsbnMetadata(
    val isbn13: String,
    val title: String?,
    val author: String?,
    val description: String?,
    val coverImageUrl: String?
)

class CompositeIsbnLookupService(
    private val delegates: List<IsbnLookupService>
) : IsbnLookupService {
    override suspend fun fetchMetadata(isbn13: String): Result<IsbnMetadataDto> = runCatching {
        val results = delegates.mapNotNull { service ->
            service.fetchMetadata(isbn13).getOrNull()
        }

        if (results.isEmpty()) {
            error("No metadata providers returned a result for ISBN $isbn13")
        }

        val merged = merge(isbn13, results)
        IsbnMetadataDto(
            isbn13 = merged.isbn13,
            title = merged.title,
            author = merged.author,
            publisher = null,
            publishedDate = null,
            description = merged.description,
            coverImageUrl = merged.coverImageUrl
        )
    }

    private fun merge(isbn13: String, sources: List<IsbnMetadataDto>): CombinedIsbnMetadata {
        val title = sources.firstNotNullOfOrNull { it.title?.takeIf(String::isNotBlank) }
        val author = sources.firstNotNullOfOrNull { it.author?.takeIf(String::isNotBlank) }
        val description = sources.firstNotNullOfOrNull { it.description?.takeIf(String::isNotBlank) }
        val cover = sources.firstNotNullOfOrNull { it.coverImageUrl?.takeIf(String::isNotBlank) }
        return CombinedIsbnMetadata(
            isbn13 = isbn13,
            title = title,
            author = author,
            description = description,
            coverImageUrl = cover
        )
    }
}
