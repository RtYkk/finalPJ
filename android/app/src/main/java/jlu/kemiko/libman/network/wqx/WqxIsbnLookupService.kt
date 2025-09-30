package jlu.kemiko.libman.network.wqx

import jlu.kemiko.libman.network.IsbnLookupService
import jlu.kemiko.libman.network.IsbnMetadataDto

internal class WqxIsbnLookupService(
    private val api: WqxApi
) : IsbnLookupService {
    override suspend fun fetchMetadata(isbn13: String): Result<IsbnMetadataDto> = runCatching {
        val response = api.searchByIsbn(keyword = isbn13)
        val item = response.data?.list.orEmpty().firstOrNull {
            it.isbn?.trim() == isbn13
        } ?: error("No WQX result for ISBN $isbn13")

        val coverUrl = item.coverurl?.substringBefore("!m")

        IsbnMetadataDto(
            isbn13 = isbn13,
            title = item.name,
            author = item.author,
            publisher = null,
            publishedDate = null,
            description = item.descript,
            coverImageUrl = coverUrl
        )
    }
}
