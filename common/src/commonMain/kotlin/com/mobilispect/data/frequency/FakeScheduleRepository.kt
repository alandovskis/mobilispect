package com.mobilispect.data.frequency

import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.schedule.ScheduleRepository
import com.mobilispect.data.stop.StopRef
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

internal class FakeScheduleRepository @Inject constructor() : ScheduleRepository {
    override fun forDayAtStopOnRouteInDirection(
        start: LocalDateTime,
        end: LocalDateTime,
        routeRef: RouteRef,
        stop: StopRef,
        direction: Direction
    ): Collection<LocalDateTime> {
        val today = start.toLocalDate()
        val departures = when (direction) {
            Direction.Inbound -> inboundDepartures(today)
            Direction.Outbound -> outboundDepartures(today)
        }

        return departures.filter { time -> !time.isBefore(start) && !time.isAfter(end) }
    }

    private fun inboundDepartures(
        today: LocalDate
    ): List<LocalDateTime> {
        val tomorrow = today.plusDays(1)

        return listOf(
            LocalDateTime.of(today, LocalTime.of(4, 45)),
            LocalDateTime.of(today, LocalTime.of(4, 57)),

            LocalDateTime.of(today, LocalTime.of(5, 10)),
            LocalDateTime.of(today, LocalTime.of(5, 22)),
            LocalDateTime.of(today, LocalTime.of(5, 34)),
            LocalDateTime.of(today, LocalTime.of(5, 45)),
            LocalDateTime.of(today, LocalTime.of(5, 57)),

            LocalDateTime.of(today, LocalTime.of(6, 9)),
            LocalDateTime.of(today, LocalTime.of(6,19)),
            LocalDateTime.of(today, LocalTime.of(6,29)),
            LocalDateTime.of(today, LocalTime.of(6,38)),
            LocalDateTime.of(today, LocalTime.of(6,48)),
            LocalDateTime.of(today, LocalTime.of(6,58)),

            LocalDateTime.of(today, LocalTime.of(7,8)),
            LocalDateTime.of(today, LocalTime.of(7, 18)),
            LocalDateTime.of(today, LocalTime.of(7, 28)),
            LocalDateTime.of(today, LocalTime.of(7, 39)),
            LocalDateTime.of(today, LocalTime.of(7, 49)),

            LocalDateTime.of(today, LocalTime.of(8, 0)),
            LocalDateTime.of(today, LocalTime.of(8, 11)),
            LocalDateTime.of(today, LocalTime.of(8, 22)),
            LocalDateTime.of(today, LocalTime.of(8, 35)),
            LocalDateTime.of(today, LocalTime.of(8, 48)),

            LocalDateTime.of(today, LocalTime.of(9, 1)),
            LocalDateTime.of(today, LocalTime.of(9, 13)),
            LocalDateTime.of(today, LocalTime.of(9, 24)),
            LocalDateTime.of(today, LocalTime.of(9, 36)),
            LocalDateTime.of(today, LocalTime.of(9, 48)),

            LocalDateTime.of(today, LocalTime.of(10, 1)),
            LocalDateTime.of(today, LocalTime.of(10, 14)),
            LocalDateTime.of(today, LocalTime.of(10, 26)),
            LocalDateTime.of(today, LocalTime.of(10, 36)),
            LocalDateTime.of(today, LocalTime.of(10, 46)),
            LocalDateTime.of(today, LocalTime.of(10, 56)),

            LocalDateTime.of(today, LocalTime.of(11, 6)),
            LocalDateTime.of(today, LocalTime.of(11, 16)),
            LocalDateTime.of(today, LocalTime.of(11, 26)),
            LocalDateTime.of(today, LocalTime.of(11, 35)),
            LocalDateTime.of(today, LocalTime.of(11, 43)),
            LocalDateTime.of(today, LocalTime.of(11, 51)),
            LocalDateTime.of(today, LocalTime.of(11, 59)),

            LocalDateTime.of(today, LocalTime.of(12, 8)),
            LocalDateTime.of(today, LocalTime.of(12, 17)),
            LocalDateTime.of(today, LocalTime.of(12, 25)),
            LocalDateTime.of(today, LocalTime.of(12, 32)),
            LocalDateTime.of(today, LocalTime.of(12, 38)),
            LocalDateTime.of(today, LocalTime.of(12, 45)),
            LocalDateTime.of(today, LocalTime.of(12, 52)),
            LocalDateTime.of(today, LocalTime.of(12, 58)),

            LocalDateTime.of(today, LocalTime.of(13, 6)),
            LocalDateTime.of(today, LocalTime.of(13, 13)),
            LocalDateTime.of(today, LocalTime.of(13, 20)),
            LocalDateTime.of(today, LocalTime.of(13, 27)),
            LocalDateTime.of(today, LocalTime.of(13, 35)),
            LocalDateTime.of(today, LocalTime.of(13, 43)),
            LocalDateTime.of(today, LocalTime.of(13, 51)),
            LocalDateTime.of(today, LocalTime.of(13, 59)),

            LocalDateTime.of(today, LocalTime.of(14, 7)),
            LocalDateTime.of(today, LocalTime.of(14, 15)),
            LocalDateTime.of(today, LocalTime.of(14, 23)),
            LocalDateTime.of(today, LocalTime.of(14, 32)),
            LocalDateTime.of(today, LocalTime.of(14, 42)),
            LocalDateTime.of(today, LocalTime.of(14, 50)),
            LocalDateTime.of(today, LocalTime.of(14, 57)),

            LocalDateTime.of(today, LocalTime.of(15, 4)),
            LocalDateTime.of(today, LocalTime.of(15, 10)),
            LocalDateTime.of(today, LocalTime.of(15, 15)),
            LocalDateTime.of(today, LocalTime.of(15, 21)),
            LocalDateTime.of(today, LocalTime.of(15, 29)),
            LocalDateTime.of(today, LocalTime.of(15, 36)),
            LocalDateTime.of(today, LocalTime.of(15, 44)),
            LocalDateTime.of(today, LocalTime.of(15, 52)),

            LocalDateTime.of(today, LocalTime.of(16, 0)),
            LocalDateTime.of(today, LocalTime.of(16, 7)),
            LocalDateTime.of(today, LocalTime.of(16, 15)),
            LocalDateTime.of(today, LocalTime.of(16, 22)),
            LocalDateTime.of(today, LocalTime.of(16, 28)),
            LocalDateTime.of(today, LocalTime.of(16, 34)),
            LocalDateTime.of(today, LocalTime.of(16, 40)),
            LocalDateTime.of(today, LocalTime.of(16, 47)),
            LocalDateTime.of(today, LocalTime.of(16, 54)),

            LocalDateTime.of(today, LocalTime.of(17, 1)),
            LocalDateTime.of(today, LocalTime.of(17, 11)),
            LocalDateTime.of(today, LocalTime.of(17, 21)),
            LocalDateTime.of(today, LocalTime.of(17, 31)),
            LocalDateTime.of(today, LocalTime.of(17, 41)),
            LocalDateTime.of(today, LocalTime.of(17, 50)),

            LocalDateTime.of(today, LocalTime.of(18, 0)),
            LocalDateTime.of(today, LocalTime.of(18, 9)),
            LocalDateTime.of(today, LocalTime.of(18, 19)),
            LocalDateTime.of(today, LocalTime.of(18, 29)),
            LocalDateTime.of(today, LocalTime.of(18, 40)),
            LocalDateTime.of(today, LocalTime.of(18, 52)),

            LocalDateTime.of(today, LocalTime.of(19, 4)),
            LocalDateTime.of(today, LocalTime.of(19, 17)),
            LocalDateTime.of(today, LocalTime.of(19, 30)),
            LocalDateTime.of(today, LocalTime.of(19, 43)),
            LocalDateTime.of(today, LocalTime.of(19, 56)),

            LocalDateTime.of(today, LocalTime.of(20, 7)),
            LocalDateTime.of(today, LocalTime.of(20, 19)),
            LocalDateTime.of(today, LocalTime.of(20, 31)),
            LocalDateTime.of(today, LocalTime.of(20, 43)),
            LocalDateTime.of(today, LocalTime.of(20, 56)),

            LocalDateTime.of(today, LocalTime.of(21, 10)),
            LocalDateTime.of(today, LocalTime.of(21, 28)),
            LocalDateTime.of(today, LocalTime.of(21, 48)),

            LocalDateTime.of(today, LocalTime.of(22, 8)),
            LocalDateTime.of(today, LocalTime.of(22, 29)),
            LocalDateTime.of(today, LocalTime.of(22, 50)),

            LocalDateTime.of(today, LocalTime.of(23, 11)),
            LocalDateTime.of(today, LocalTime.of(23, 33)),
            LocalDateTime.of(today, LocalTime.of(23, 59)),

            LocalDateTime.of(tomorrow, LocalTime.of(0, 27)),
            LocalDateTime.of(tomorrow, LocalTime.of(0, 55)),

            LocalDateTime.of(tomorrow, LocalTime.of(1, 22)),
        )
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