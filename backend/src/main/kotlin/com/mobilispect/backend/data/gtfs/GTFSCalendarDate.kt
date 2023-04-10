package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class GTFSCalendarDate(
    val service_id: String,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
    val exception_type: Int
) {
    companion object {
        const val ADDED: Int = 1
        const val REMOVED: Int = 2
    }
}
