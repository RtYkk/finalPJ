package jlu.kemiko.libman.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import jlu.kemiko.libman.data.model.LoanStatus

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["isbn13"],
            childColumns = ["isbn13"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatronEntity::class,
            parentColumns = ["student_id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["isbn13"]),
        Index(value = ["student_id"]),
        Index(value = ["status"])
    ]
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "loan_id")
    val loanId: Long = 0,
    @ColumnInfo(name = "isbn13")
    val isbn13: String,
    @ColumnInfo(name = "student_id")
    val studentId: String,
    val status: LoanStatus,
    @ColumnInfo(name = "borrowed_at")
    val borrowedAt: Instant,
    @ColumnInfo(name = "due_at")
    val dueAt: Instant?,
    @ColumnInfo(name = "returned_at")
    val returnedAt: Instant?
)
