package jlu.kemiko.libman.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jlu.kemiko.libman.data.local.entity.PatronEntity

@Dao
interface PatronDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(patron: PatronEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(patrons: List<PatronEntity>)

    @Query("SELECT * FROM patrons WHERE student_id = :studentId LIMIT 1")
    suspend fun getByStudentId(studentId: String): PatronEntity?
}
