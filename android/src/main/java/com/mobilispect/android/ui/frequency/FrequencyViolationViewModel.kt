package com.mobilispect.android.ui.frequency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobilispect.data.frequency.Direction
import com.mobilispect.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.frequency.FindFrequencyViolationsOnDayAtStopUseCase
import com.mobilispect.data.frequency.FrequencyViolation
import com.mobilispect.data.stop.StopRef
import com.mobilispect.domain.time.FormatTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FrequencyViolationViewModel @Inject constructor(
    private val frequencyViolationUseCase: FindFrequencyViolationsOnDayAtStopUseCase,
    private val formatTimeUseCase: FormatTimeUseCase,
) : ViewModel() {
    private var _violations: MutableLiveData<FrequencyViolationUIState> = MutableLiveData()
    val violations: LiveData<FrequencyViolationUIState> = _violations

    fun findFrequencyViolationsAgainstScheduleForFirstStopAndDay(routeRef: RouteRef) {
        val start = LocalDateTime.of(2022, 7, 7, 0, 0)
        val stopRef = StopRef(geohash = "abcd", name = "test")
        val inbound = frequencyViolationUseCase.invoke(
            start = start,
            routeRef = routeRef,
            stopRef = stopRef,
            direction = Direction.Inbound,
            commitment = STM_FREQUENCY_COMMITMENT
        )
            .map(::violationUIState)
        val outbound = frequencyViolationUseCase.invoke(
            start = start,
            routeRef = routeRef,
            stopRef = stopRef,
            direction = Direction.Outbound,
            commitment = STM_FREQUENCY_COMMITMENT
        )
            .map(::violationUIState)

        val uiState = FrequencyViolationUIState(
            inbound = inbound,
            outbound = outbound,
        )

        _violations.postValue(uiState)
    }

    private fun violationUIState(violations: List<FrequencyViolation>) =
        violations.map {
            FrequencyViolationInstanceUIState(
                start = formatTimeUseCase.invoke(it.start),
                end = formatTimeUseCase.invoke(it.end),
                violatedBy_m = it.duration.toMinutes()
            )
        }
}

data class FrequencyViolationUIState(
    val inbound: Result<List<FrequencyViolationInstanceUIState>>,
    val outbound: Result<List<FrequencyViolationInstanceUIState>>,
)

data class FrequencyViolationInstanceUIState(
    val start: String,
    val end: String,
    val violatedBy_m: Long
)