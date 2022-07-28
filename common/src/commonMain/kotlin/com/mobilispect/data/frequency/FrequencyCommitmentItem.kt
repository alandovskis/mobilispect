package com.mobilispect.data.frequency

import com.mobilispect.data.routes.RouteRef
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime


enum class Direction {
    Inbound,
    Outbound;
}

data class DirectionTime(
    val direction: Direction?,
    val start: LocalTime,
    val end: LocalTime) {

    companion object {
        fun both(start: LocalTime, end: LocalTime): Collection<DirectionTime> {
            return listOf(
                DirectionTime(
                    direction = null,
                    start = start,
                    end = end
                ),
            )
        }
    }
}

data class FrequencyCommitmentItem(
    val daysOfWeek: Collection<DayOfWeek>,
    val frequency: Duration,
    val routes: List<RouteRef>,
    val directions: Collection<DirectionTime>,
)