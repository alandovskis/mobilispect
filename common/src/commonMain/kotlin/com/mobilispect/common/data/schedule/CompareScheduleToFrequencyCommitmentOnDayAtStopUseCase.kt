package com.mobilispect.common.data.schedule

import com.mobilispect.common.data.frequency.Direction
import com.mobilispect.common.data.frequency.FrequencyCommitment
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.stop.StopRef
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class CompareScheduleToFrequencyCommitmentOnDayAtStopUseCase @Inject constructor(private val scheduleRepo: ScheduleRepository) {
    operator fun invoke(
        start: LocalDateTime,
        routeRef: RouteRef,
        stopRef: StopRef,
        direction: Direction,
        commitment: FrequencyCommitment
    ): Result<List<FrequencyViolation>> {
        val relevantCommitments =
            commitment.spans.filter { span -> span.routes.contains(routeRef) }
                .filter { span -> span.daysOfWeek.contains(start.dayOfWeek) }
        check(relevantCommitments.size == 1)
        val relevantCommitment = relevantCommitments.first()

        val directionTimes =
            relevantCommitment.directions.filter { directionTime -> directionTime.direction == direction || directionTime.direction == null }
        /*.filter { directionTime ->
            start.dayOfWeek == start.toLocalDate().dayOfWeek
                    && !directionTime.start.isAfter(start.toLocalTime())
        }*/
        check(directionTimes.size == 1)
        val directionTime = directionTimes.first()

        val adjustedStart = LocalDateTime.of(start.toLocalDate(), directionTime.start)
        val adjustedEnd = LocalDateTime.of(start.toLocalDate(), directionTime.end)

        val sentinel = Duration.ZERO
        val departures = scheduleRepo.forDayAtStopOnRouteInDirection(
            adjustedStart,
            adjustedEnd,
            routeRef,
            stopRef,
            direction
        )
        if (departures.isEmpty()) {
            return Result.failure(NoDeparturesFound)
        }

        val violations = departures.windowed(2, 1, true) { dateTimes ->
            if (dateTimes.size >= 2) {
                return@windowed FrequencyViolation(
                    start = dateTimes[0],
                    end = dateTimes[1], duration = Duration.between(dateTimes[0], dateTimes[1])
                )
            } else {
                return@windowed FrequencyViolation(
                    start = LocalDateTime.MIN,
                    end = LocalDateTime.MAX, duration = sentinel
                )
            }
        }
            .filter { it.duration != sentinel }
            .filter {
                it.duration > relevantCommitment.frequency
            }

        println(departures)
        return Result.success(violations)
    }
}

data class FrequencyViolation(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val duration: Duration
)

object NoDeparturesFound : Exception()