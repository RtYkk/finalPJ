package jlu.kemiko.libman.data.local.db

import androidx.room.TypeConverter
import java.time.Instant
import jlu.kemiko.libman.data.model.LoanStatus

/**
 * Shared Room type converters for java.time and enum persistence.
 */
class RoomTypeConverters {
    @TypeConverter
    fun toInstant(epochMillis: Long?): Instant? = epochMillis?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun toLoanStatus(raw: String?): LoanStatus? = raw?.let { LoanStatus.valueOf(it) }

    @TypeConverter
    fun fromLoanStatus(status: LoanStatus?): String? = status?.name
}
