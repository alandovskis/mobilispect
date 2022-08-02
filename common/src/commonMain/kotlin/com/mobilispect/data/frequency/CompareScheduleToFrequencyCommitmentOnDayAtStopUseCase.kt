package com.mobilispect.data.frequency

import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.schedule.ScheduleRepository
import com.mobilispect.data.schedule.ScheduledStop
import com.mobilispect.data.stop.StopRef
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

        //TODO: Determine what to do for frequency, especially for scheduled -> frequency and vice
        // versa
        val violations = departures.filter { stop -> stop.time is ScheduledStop.Time.Scheduled }
            .windowed(2, 1, true) { dateTimes ->
                return@windowed if (dateTimes.size >= 2) {
                    val intervalStart = localDateTime(dateTimes[0])
                    val intervalEnd = localDateTime(dateTimes[1])

                    if ((intervalStart != null) && (intervalEnd != null)) {
                        val duration = Duration.between(
                            intervalStart,
                            intervalEnd
                        )

                        return@windowed FrequencyViolation(
                            start = intervalStart,
                            end = intervalEnd, duration = duration
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            .filterNotNull()
            .filter { it.duration != sentinel }
            .filter {
                it.duration > relevantCommitment.frequency
            }

        println(departures)
        return Result.success(violations)
    }

    private fun localDateTime(dateTime: ScheduledStop) =
        (dateTime.time as ScheduledStop.Time.Scheduled).dateTime
}

data class FrequencyViolation(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val duration: Duration
)

object NoDeparturesFound : Exception()