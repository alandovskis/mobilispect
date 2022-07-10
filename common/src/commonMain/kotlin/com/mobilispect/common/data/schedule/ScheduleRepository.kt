package com.mobilispect.common.data.schedule

import com.mobilispect.common.data.frequency.Direction
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.stop.StopRef
import java.time.LocalDateTime

interface ScheduleRepository {
    fun forDayAtStopOnRouteInDirection(
        start: LocalDateTime,
        end: LocalDateTime,
        routeRef: RouteRef,
        stop: StopRef,
        direction: Direction
    ): Collection<LocalDateTime>
}
