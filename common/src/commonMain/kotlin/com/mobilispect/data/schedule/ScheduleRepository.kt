package com.mobilispect.data.schedule

import com.mobilispect.data.frequency.Direction
import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.stop.StopRef
import java.time.LocalDateTime

interface ScheduleRepository {
    fun forDayAtStopOnRouteInDirection(
        start: LocalDateTime,
        end: LocalDateTime,
        routeRef: RouteRef,
        stopRef: StopRef,
        direction: Direction
    ): Collection<ScheduledStop>
}
