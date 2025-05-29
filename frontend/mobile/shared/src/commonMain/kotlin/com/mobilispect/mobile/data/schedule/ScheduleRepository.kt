package com.mobilispect.mobile.data.schedule

import com.mobilispect.mobile.data.stop.StopRef
import java.time.LocalDateTime

interface ScheduleRepository {
    fun forDayAtStopOnRouteInDirection(
        start: LocalDateTime,
        end: LocalDateTime,
        routeRef: String,
        stopRef: StopRef,
        direction: Direction
    ): Collection<ScheduledStop>
}
