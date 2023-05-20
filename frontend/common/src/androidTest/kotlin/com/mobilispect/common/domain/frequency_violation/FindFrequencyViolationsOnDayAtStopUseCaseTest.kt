package com.mobilispect.common.domain.frequency_violation

import com.mobilispect.common.data.frequency_commitment.STM_FREQUENCY_COMMITMENT
import com.mobilispect.common.data.schedule.Direction
import com.mobilispect.common.data.schedule.FakeScheduleRepository
import org.junit.Test
import java.time.LocalDateTime

internal class FindFrequencyViolationsOnDayAtStopUseCaseTest {
    @Test
    fun violationsFromScheduledStops() {
        FindFrequencyViolationsOnDayAtStopUseCase(FakeScheduleRepository())
            .invoke(
                start = LocalDateTime.of(2022, 7, 7, 6, 0, 0),
                routeRef = "r-f25em-141",
                stopRef = com.mobilispect.common.data.stop.StopRef(geohash = "abcd", name = "test"),
                direction = Direction.Outbound,
                commitment = STM_FREQUENCY_COMMITMENT
            )
    }
}