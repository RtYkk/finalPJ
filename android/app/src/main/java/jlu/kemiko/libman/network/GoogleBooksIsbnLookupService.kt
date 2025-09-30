package jlu.kemiko.libman.network

import jlu.kemiko.libman.BuildConfig

internal class GoogleBooksIsbnLookupService(
    private val api: GoogleBooksApi,
    private val apiKeyProvider: () -> String = { BuildConfig.GOOGLE_BOOKS_API_KEY }
) : IsbnLookupService {

    override suspend fun fetchMetadata(isbn13: String): Result<IsbnMetadataDto> = runCatching {
        val response = api.lookupIsbn(
            query = "isbn:$isbn13",
            apiKey = apiKeyProvider().takeIf { it.isNotBlank() }
        )
        val volumeInfo = response.items.orEmpty().firstOrNull()?.volumeInfo
            ?: error("No Google Books volume found for ISBN $isbn13")

        IsbnMetadataDto(
            isbn13 = isbn13,
            title = volumeInfo.title,
            author = volumeInfo.authors?.joinToString(separator = ", "),
            publisher = null,
            publishedDate = null,
            description = volumeInfo.description,
            coverImageUrl = volumeInfo.imageLinks?.thumbnail
        )
    }
}
