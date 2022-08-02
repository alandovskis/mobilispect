package com.mobilispect.data.frequency

import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.stop.StopRef
import org.junit.Test
import java.time.LocalDateTime

internal class CompareScheduleToFrequencyCommitmentOnDayAtStopUseCaseTest {
    @Test
    fun invoke() {
        CompareScheduleToFrequencyCommitmentOnDayAtStopUseCase(FakeScheduleRepository())
            .invoke(
                start = LocalDateTime.of(2022, 7, 7, 6, 0, 0),
                routeRef = RouteRef(geohash = "f25em", routeNumber = "141"),
                stopRef = StopRef(geohash = "abcd", name = "test"),
                direction = Direction.Outbound,
                commitment = STM_FREQUENCY_COMMITMENT
            )
    }
}

