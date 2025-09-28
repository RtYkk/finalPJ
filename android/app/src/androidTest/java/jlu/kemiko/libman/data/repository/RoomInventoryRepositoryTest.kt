package jlu.kemiko.libman.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import jlu.kemiko.libman.data.local.db.LibmanDatabase
import jlu.kemiko.libman.data.local.db.RoomTypeConverters
import jlu.kemiko.libman.data.model.Book
import jlu.kemiko.libman.data.model.Patron
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RoomInventoryRepositoryTest {

    private lateinit var context: Context
    private lateinit var database: LibmanDatabase
    private lateinit var repository: RoomInventoryRepository
    private val fixedInstant = Instant.parse("2024-01-01T00:00:00Z")

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, LibmanDatabase::class.java)
            .addTypeConverter(RoomTypeConverters())
            .allowMainThreadQueries()
            .build()
        repository = RoomInventoryRepository(
            database = database,
            clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun borrowBook_decrements_available_count_and_creates_loan() = runTest {
        prepareSeedData()

        val result = repository.borrowBook(VALID_ISBN, VALID_STUDENT_ID)
        assertTrue(result.isSuccess)

        val updatedBook = repository.fetchBookByIsbn(VALID_ISBN)!!
        assertEquals(1, updatedBook.availableCount)

        val latestLoan = database.loanDao().getLatest()
        assertEquals(VALID_ISBN, latestLoan?.isbn13)
        assertEquals(VALID_STUDENT_ID, latestLoan?.studentId)
    }

    @Test
    fun returnBook_increments_available_count() = runTest {
        prepareSeedData()
        repository.borrowBook(VALID_ISBN, VALID_STUDENT_ID)
        val loanId = database.loanDao().getLatest()!!.loanId

        val result = repository.returnBook(loanId)
        assertTrue(result.isSuccess)

        val book = repository.fetchBookByIsbn(VALID_ISBN)!!
        assertEquals(2, book.availableCount)
    }

    private suspend fun prepareSeedData() {
        repository.upsertBooks(
            Book(
                isbn13 = VALID_ISBN,
                title = "Test Book",
                author = "Tester",
                copyCount = 2,
                availableCount = 2,
                metadataFetchedAt = null
            )
        )
        repository.upsertPatrons(
            Patron(
                studentId = VALID_STUDENT_ID,
                name = "Test Patron",
                joinedAt = 0L
            )
        )
    }

    companion object {
        private const val VALID_ISBN = "9780306406157"
        private const val VALID_STUDENT_ID = "12345678"
    }
}
