package com.mobilispect.common.data.frequency

import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.schedule.ScheduleRepository
import com.mobilispect.common.data.stop.StopRef
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class FakeScheduleRepository @Inject constructor() : ScheduleRepository {
    override fun forDayAtStopOnRouteInDirection(
        start: LocalDateTime,
        end: LocalDateTime,
        routeRef: RouteRef,
        stop: StopRef,
        direction: Direction
    ): Collection<LocalDateTime> {
        val today = start.toLocalDate()
        val departures = when (direction) {
            Direction.Inbound -> listOf()
            Direction.Outbound -> outboundDepartures(today)
        }

        return departures.filter { time -> !time.isBefore(start) && !time.isAfter(end) }
    }

    private fun outboundDepartures(
        today: LocalDate
    ): List<LocalDateTime> {
        val tomorrow = today.plusDays(1)
        return listOf(
            LocalDateTime.of(today, LocalTime.of(4, 48)),

            LocalDateTime.of(today, LocalTime.of(5, 9)),
            LocalDateTime.of(today, LocalTime.of(5, 23)),
            LocalDateTime.of(today, LocalTime.of(5, 43)),

            LocalDateTime.of(today, LocalTime.of(6, 0)),
            LocalDateTime.of(today, LocalTime.of(6, 14)),
            LocalDateTime.of(today, LocalTime.of(6, 25)),
            LocalDateTime.of(today, LocalTime.of(6, 36)),
            LocalDateTime.of(today, LocalTime.of(6, 47)),

            LocalDateTime.of(today, LocalTime.of(7, 10)),
            LocalDateTime.of(today, LocalTime.of(7, 26)),
            LocalDateTime.of(today, LocalTime.of(7, 43)),

            LocalDateTime.of(today, LocalTime.of(8, 0)),
            LocalDateTime.of(today, LocalTime.of(8, 14)),
            LocalDateTime.of(today, LocalTime.of(8, 27)),
            LocalDateTime.of(today, LocalTime.of(8, 40)),
            LocalDateTime.of(today, LocalTime.of(8, 52)),

            LocalDateTime.of(today, LocalTime.of(9, 3)),
            LocalDateTime.of(today, LocalTime.of(9, 13)),
            LocalDateTime.of(today, LocalTime.of(9, 23)),
            LocalDateTime.of(today, LocalTime.of(9, 33)),
            LocalDateTime.of(today, LocalTime.of(9, 43)),
            LocalDateTime.of(today, LocalTime.of(9, 53)),

            LocalDateTime.of(today, LocalTime.of(10, 3)),
            LocalDateTime.of(today, LocalTime.of(10, 13)),
            LocalDateTime.of(today, LocalTime.of(10, 23)),
            LocalDateTime.of(today, LocalTime.of(10, 33)),
            LocalDateTime.of(today, LocalTime.of(10, 42)),
            LocalDateTime.of(today, LocalTime.of(10, 50)),
            LocalDateTime.of(today, LocalTime.of(10, 58)),

            LocalDateTime.of(today, LocalTime.of(11, 6)),
            LocalDateTime.of(today, LocalTime.of(11, 15)),
            LocalDateTime.of(today, LocalTime.of(11, 23)),
            LocalDateTime.of(today, LocalTime.of(11, 31)),
            LocalDateTime.of(today, LocalTime.of(11, 38)),
            LocalDateTime.of(today, LocalTime.of(11, 44)),
            LocalDateTime.of(today, LocalTime.of(11, 51)),

            LocalDateTime.of(today, LocalTime.of(12, 0)),
            LocalDateTime.of(today, LocalTime.of(12, 9)),
            LocalDateTime.of(today, LocalTime.of(12, 17)),
            LocalDateTime.of(today, LocalTime.of(12, 25)),
            LocalDateTime.of(today, LocalTime.of(12, 33)),
            LocalDateTime.of(today, LocalTime.of(12, 41)),
            LocalDateTime.of(today, LocalTime.of(12, 49)),
            LocalDateTime.of(today, LocalTime.of(12, 57)),

            LocalDateTime.of(today, LocalTime.of(13, 5)),
            LocalDateTime.of(today, LocalTime.of(13, 13)),
            LocalDateTime.of(today, LocalTime.of(13, 20)),
            LocalDateTime.of(today, LocalTime.of(13, 27)),
            LocalDateTime.of(today, LocalTime.of(13, 34)),
            LocalDateTime.of(today, LocalTime.of(13, 40)),
            LocalDateTime.of(today, LocalTime.of(13, 48)),
            LocalDateTime.of(today, LocalTime.of(13, 55)),

            LocalDateTime.of(today, LocalTime.of(14, 3)),
            LocalDateTime.of(today, LocalTime.of(14, 11)),
            LocalDateTime.of(today, LocalTime.of(14, 18)),
            LocalDateTime.of(today, LocalTime.of(14, 25)),
            LocalDateTime.of(today, LocalTime.of(14, 33)),
            LocalDateTime.of(today, LocalTime.of(14, 40)),
            LocalDateTime.of(today, LocalTime.of(14, 47)),
            LocalDateTime.of(today, LocalTime.of(14, 55)),

            LocalDateTime.of(today, LocalTime.of(15, 3)),
            LocalDateTime.of(today, LocalTime.of(15, 10)),
            LocalDateTime.of(today, LocalTime.of(15, 17)),
            LocalDateTime.of(today, LocalTime.of(15, 23)),
            LocalDateTime.of(today, LocalTime.of(15, 29)),
            LocalDateTime.of(today, LocalTime.of(15, 35)),
            LocalDateTime.of(today, LocalTime.of(15, 42)),
            LocalDateTime.of(today, LocalTime.of(15, 49)),
            LocalDateTime.of(today, LocalTime.of(15, 56)),

            LocalDateTime.of(today, LocalTime.of(16, 3)),
            LocalDateTime.of(today, LocalTime.of(16, 10)),
            LocalDateTime.of(today, LocalTime.of(16, 17)),
            LocalDateTime.of(today, LocalTime.of(16, 23)),
            LocalDateTime.of(today, LocalTime.of(16, 29)),
            LocalDateTime.of(today, LocalTime.of(16, 34)),
            LocalDateTime.of(today, LocalTime.of(16, 39)),
            LocalDateTime.of(today, LocalTime.of(16, 44)),
            LocalDateTime.of(today, LocalTime.of(16, 50)),
            LocalDateTime.of(today, LocalTime.of(16, 55)),

            LocalDateTime.of(today, LocalTime.of(17, 0)),
            LocalDateTime.of(today, LocalTime.of(17, 7)),
            LocalDateTime.of(today, LocalTime.of(17, 13)),
            LocalDateTime.of(today, LocalTime.of(17, 20)),
            LocalDateTime.of(today, LocalTime.of(17, 28)),
            LocalDateTime.of(today, LocalTime.of(17, 35)),
            LocalDateTime.of(today, LocalTime.of(17, 41)),
            LocalDateTime.of(today, LocalTime.of(17, 47)),
            LocalDateTime.of(today, LocalTime.of(17, 54)),

            LocalDateTime.of(today, LocalTime.of(18, 1)),
            LocalDateTime.of(today, LocalTime.of(18, 7)),
            LocalDateTime.of(today, LocalTime.of(18, 13)),
            LocalDateTime.of(today, LocalTime.of(18, 20)),
            LocalDateTime.of(today, LocalTime.of(18, 28)),
            LocalDateTime.of(today, LocalTime.of(18, 36)),
            LocalDateTime.of(today, LocalTime.of(18, 44)),
            LocalDateTime.of(today, LocalTime.of(18, 53)),

            LocalDateTime.of(today, LocalTime.of(19, 3)),
            LocalDateTime.of(today, LocalTime.of(19, 12)),
            LocalDateTime.of(today, LocalTime.of(19, 21)),
            LocalDateTime.of(today, LocalTime.of(19, 31)),
            LocalDateTime.of(today, LocalTime.of(19, 41)),
            LocalDateTime.of(today, LocalTime.of(19, 52)),

            LocalDateTime.of(today, LocalTime.of(20, 5)),
            LocalDateTime.of(today, LocalTime.of(20, 19)),
            LocalDateTime.of(today, LocalTime.of(20, 34)),
            LocalDateTime.of(today, LocalTime.of(20, 49)),

            LocalDateTime.of(today, LocalTime.of(21, 3)),
            LocalDateTime.of(today, LocalTime.of(21, 16)),
            LocalDateTime.of(today, LocalTime.of(21, 30)),
            LocalDateTime.of(today, LocalTime.of(21, 44)),

            LocalDateTime.of(today, LocalTime.of(22, 3)),
            LocalDateTime.of(today, LocalTime.of(22, 22)),
            LocalDateTime.of(today, LocalTime.of(22, 41)),

            LocalDateTime.of(today, LocalTime.of(23, 1)),
            LocalDateTime.of(today, LocalTime.of(23, 21)),
            LocalDateTime.of(today, LocalTime.of(23, 43)),

            LocalDateTime.of(tomorrow, LocalTime.of(0, 10)),
            LocalDateTime.of(tomorrow, LocalTime.of(0, 37)),
        )
    }
}