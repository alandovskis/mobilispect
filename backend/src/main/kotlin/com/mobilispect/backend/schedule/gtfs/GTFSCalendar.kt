package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class GTFSCalendar(
    val service_id: String,
    val monday: Int,
    val tuesday: Int,
    val wednesday: Int,
    val thursday: Int,
    val friday: Int,
    val saturday: Int,
    val sunday: Int,
    @Serializable(with = LocalDateSerializer::class) val start_date: LocalDate,
    @Serializable(with = LocalDateSerializer::class) val end_date: LocalDate
)
