package jlu.kemiko.libman.common.di

import android.content.Context
import jlu.kemiko.libman.data.local.db.LibmanDatabaseFactory
import jlu.kemiko.libman.data.repository.InventoryRepository
import jlu.kemiko.libman.data.repository.RoomInventoryRepository
import jlu.kemiko.libman.network.CompositeIsbnLookupService
import jlu.kemiko.libman.network.GoogleBooksApi
import jlu.kemiko.libman.network.GoogleBooksIsbnLookupService
import jlu.kemiko.libman.network.IsbnLookupService
import jlu.kemiko.libman.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import jlu.kemiko.libman.network.wqx.WqxApi
import jlu.kemiko.libman.network.wqx.WqxIsbnLookupService

/**
 * Simple service locator used until a full dependency injection framework is introduced.
 */
interface AppContainer {
    val inventoryRepository: InventoryRepository
    val isbnLookupService: IsbnLookupService
}

/**
 * Default implementation wiring repositories and data sources.
 * TODO: Replace with Hilt or another DI solution when feature work begins.
 */
class DefaultAppContainer(context: Context) : AppContainer {
    private val database = LibmanDatabaseFactory.create(context)

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addInterceptor(logging)
        }

        builder.build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val googleBooksRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val googleBooksApi: GoogleBooksApi by lazy {
        googleBooksRetrofit.create(GoogleBooksApi::class.java)
    }

    private val wqxRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://lib-jlu.wqxuetang.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val wqxApi: WqxApi by lazy {
        wqxRetrofit.create(WqxApi::class.java)
    }

    override val inventoryRepository: InventoryRepository by lazy {
        RoomInventoryRepository(database)
    }

    override val isbnLookupService: IsbnLookupService by lazy {
        CompositeIsbnLookupService(
            delegates = listOf(
                GoogleBooksIsbnLookupService(api = googleBooksApi),
                WqxIsbnLookupService(api = wqxApi)
            )
        )
    }
}
