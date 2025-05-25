@file:Suppress("PackageNaming", "MagicNumber")

package com.mobilispect.mobile.android.ui.frequency_violation

import androidx.lifecycle.ViewModel
import com.mobilispect.mobile.data.frequency_commitment.STM_FREQUENCY_COMMITMENT
import com.mobilispect.mobile.data.schedule.Direction
import com.mobilispect.mobile.data.stop.StopRef
import com.mobilispect.mobile.domain.frequency_violation.FindFrequencyViolationsOnDayAtStopUseCase
import com.mobilispect.mobile.domain.frequency_violation.FrequencyViolation
import com.mobilispect.mobile.domain.time.FormatTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class FrequencyViolationViewModel @Inject constructor(
    private val frequencyViolationUseCase: FindFrequencyViolationsOnDayAtStopUseCase,
    private val formatTimeUseCase: FormatTimeUseCase,
) : ViewModel() {
    private val _violations: MutableStateFlow<FrequencyViolationUIState> = MutableStateFlow(
        FrequencyViolationUIState(
            inbound = Result.success(emptyList()),
            outbound = Result.success(emptyList())
        )
    )
    val violations: Flow<FrequencyViolationUIState> = _violations

    fun findFrequencyViolationsAgainstScheduleForFirstStopAndDay(routeRef: String) {
        val start = LocalDateTime.of(
            LocalDate.of(2022, 7, 7),
            LocalTime.MIDNIGHT
        )
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

        _violations.value = uiState
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
    @Suppress("ConstructorParameterNaming")
    val violatedBy_m: Long
)
