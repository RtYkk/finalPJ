package jlu.kemiko.libman.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "patrons",
    indices = [Index(value = ["name"])]
)
data class PatronEntity(
    @PrimaryKey
    @ColumnInfo(name = "student_id")
    val studentId: String,
    val name: String,
    @ColumnInfo(name = "joined_at")
    val joinedAt: Long
)
