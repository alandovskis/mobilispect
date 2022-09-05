package com.mobilispect.common.data.schedule

import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.stop.StopRef
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
