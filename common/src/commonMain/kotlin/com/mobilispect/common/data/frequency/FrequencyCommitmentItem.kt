package com.mobilispect.common.data.frequency

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime


enum class Direction {
    Inbound,
    Outbound;
}

data class DirectionTime(
    val direction: Direction,
    val start: LocalTime,
    val end: LocalTime) {

    companion object {
        fun both(start: LocalTime, end: LocalTime): Collection<DirectionTime> {
            return listOf(
                DirectionTime(
                    direction = Direction.Inbound,
                    start = start,
                    end = end
                ),
                DirectionTime(
                    direction = Direction.Outbound,
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
    val routes: Collection<String>,
    val directions: Collection<DirectionTime>,
)