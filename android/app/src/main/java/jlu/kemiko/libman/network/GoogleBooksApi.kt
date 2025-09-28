package jlu.kemiko.libman.network

import retrofit2.http.GET
import retrofit2.http.Query

internal interface GoogleBooksApi {
    @GET("volumes")
    suspend fun lookupIsbn(
        @Query("q") query: String,
        @Query("fields") fields: String = FIELDS,
        @Query("maxResults") maxResults: Int = 1,
        @Query("key") apiKey: String? = null
    ): GoogleBooksResponse

    companion object {
        private const val FIELDS = "items(volumeInfo/title,volumeInfo/authors)"
    }
}

data class GoogleBooksResponse(
    val items: List<GoogleBooksVolume>?
)

data class GoogleBooksVolume(
    val volumeInfo: GoogleBooksVolumeInfo?
)

data class GoogleBooksVolumeInfo(
    val title: String?,
    val authors: List<String>?
)
