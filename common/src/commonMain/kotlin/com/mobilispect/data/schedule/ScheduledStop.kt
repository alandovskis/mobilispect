package com.mobilispect.data.schedule

import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.stop.StopRef
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

data class ScheduledStop(val routeRef: RouteRef, val stopRef: StopRef, val time: Time) {
    sealed interface Time {
        data class Scheduled(val departsAt: LocalDateTime?, val arrivesAt: LocalDateTime?) : Time {
            val dateTime: LocalDateTime?
                get() = departsAt ?: arrivesAt
        }

        data class Frequency(val start: LocalTime, val end: LocalTime, val duration: Duration) : Time
    }
}
