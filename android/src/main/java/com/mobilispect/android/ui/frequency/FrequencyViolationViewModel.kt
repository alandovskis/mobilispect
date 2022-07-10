package com.mobilispect.android.ui.frequency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobilispect.common.data.frequency.Direction
import com.mobilispect.common.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.schedule.CompareScheduleToFrequencyCommitmentOnDayAtStopUseCase
import com.mobilispect.common.data.stop.StopRef
import com.mobilispect.common.domain.time.FormatTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FrequencyViolationViewModel @Inject constructor(
    private val compareUseCase: CompareScheduleToFrequencyCommitmentOnDayAtStopUseCase,
    private val formatTimeUseCase: FormatTimeUseCase,
) : ViewModel() {
    var violations: MutableLiveData<FrequencyViolationUIState> = MutableLiveData()
        private set


    fun findFrequencyViolationsAgainstScheduleForFirstStopAndDay(routeRef: RouteRef) {
        val start = LocalDateTime.of(2022, 7, 7, 0, 0)
        val stopRef = StopRef(geohash = "abcd", name = "test")
        val outbound = compareUseCase.invoke(
            start = start,
            routeRef = routeRef,
            stopRef = stopRef,
            direction = Direction.Outbound,
            commitment = STM_FREQUENCY_COMMITMENT
        )
        .map { durations ->
            durations.map {
                FrequencyViolationInstanceUIState(
                    start = formatTimeUseCase.invoke(it.start),
                    end = formatTimeUseCase.invoke(it.end),
                    violatedBy_m = it.duration.toMinutes()
                )
            }

        }

        val uiState = FrequencyViolationUIState(
            directions = mapOf(
/*                Direction.Inbound to compareUseCase.invoke(
                    start = start,
                    routeRef = routeRef,
                    stopRef = stopRef,
                    direction = Direction.Inbound,
                    commitment = STM_FREQUENCY_COMMITMENT
                ),*/
                Direction.Outbound to outbound,
            )
        )

        violations.postValue(uiState)
    }

}

data class FrequencyViolationUIState(
    val directions: Map<Direction, Result<List<FrequencyViolationInstanceUIState>>>,
)

data class FrequencyViolationInstanceUIState(
    val start: String,
    val end: String,
    val violatedBy_m: Long
)