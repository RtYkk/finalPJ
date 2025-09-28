package jlu.kemiko.libman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.Instant
import jlu.kemiko.libman.data.local.entity.LoanEntity
import jlu.kemiko.libman.data.model.LoanStatus

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(loan: LoanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(loans: List<LoanEntity>): List<Long>

    @Query("SELECT * FROM loans WHERE loan_id = :loanId LIMIT 1")
    suspend fun getById(loanId: Long): LoanEntity?

    @Query("SELECT * FROM loans ORDER BY loan_id DESC LIMIT 1")
    suspend fun getLatest(): LoanEntity?

    @Query(
        """
        UPDATE loans
        SET status = :status,
            returned_at = :returnedAt
        WHERE loan_id = :loanId
        """
    )
    suspend fun markReturned(loanId: Long, status: LoanStatus, returnedAt: Instant?)
}
