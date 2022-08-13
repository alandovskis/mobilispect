package com.mobilispect.data.frequency

import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.schedule.FakeScheduleRepository
import com.mobilispect.common.data.stop.StopRef
import com.mobilispect.common.domain.frequency_violation.FindFrequencyViolationsOnDayAtStopUseCase
import org.junit.Test
import java.time.LocalDateTime

internal class FindFrequencyViolationsOnDayAtStopUseCaseTest {
    @Test
    fun violationsFromScheduledStops() {
        FindFrequencyViolationsOnDayAtStopUseCase(FakeScheduleRepository())
            .invoke(
                start = LocalDateTime.of(2022, 7, 7, 6, 0, 0),
                routeRef = RouteRef(geohash = "f25em", routeNumber = "141"),
                stopRef = StopRef(geohash = "abcd", name = "test"),
                direction = Direction.Outbound,
                commitment = STM_FREQUENCY_COMMITMENT
            )
    }
}

