package jlu.kemiko.libman.network.wqx

import retrofit2.http.GET
import retrofit2.http.Query

internal interface WqxApi {
    @GET("v2/search/index")
    suspend fun searchByIsbn(
        @Query("kw") keyword: String,
        @Query("pn") pageNumber: Int = 1,
        @Query("size") size: Int = 1,
        @Query("webType") webType: Int = 2
    ): WqxSearchResponse
}

data class WqxSearchResponse(
    val errcode: Int?,
    val errmsg: String?,
    val code: Int?,
    val message: String?,
    val data: WqxSearchData?
)

data class WqxSearchData(
    val list: List<WqxSearchItem>?
)

data class WqxSearchItem(
    val isbn: String?,
    val name: String?,
    val author: String?,
    val descript: String?,
    val coverurl: String?
)
