package com.mobilispect.android.ui.frequency

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilispect.android.R
import com.mobilispect.android.data.routes.RouteRepository
import com.mobilispect.common.data.frequency.DirectionTime
import com.mobilispect.common.data.frequency.FrequencyCommitmentItem
import com.mobilispect.common.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.time.WEEKDAYS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject


@HiltViewModel
class FrequencyCommitmentViewModel @Inject constructor(private val routeRepository: RouteRepository): ViewModel() {
    private var _details = MutableLiveData<FrequencyCommitmentUIState>()
    val details: LiveData<FrequencyCommitmentUIState> = _details

    fun details() {
        viewModelScope.launch {
            val frequencyCommitment = STM_FREQUENCY_COMMITMENT
            val items = frequencyCommitment.items().map { item ->
                @StringRes
                var daysOfTheWeek = 0

                if (item.daysOfWeek == WEEKDAYS) {
                    daysOfTheWeek = R.string.weekdays
                }

                FrequencyCommitmentItemUIState(
                    daysOfTheWeek = daysOfTheWeek,
                    directions = directions(item),
                    frequency = FrequencyCommitmentFrequencyUIState(
                        every = R.string.every,
                        frequency = item.frequency.toMinutes(),
                        minutesOrLess = R.string.minutes_or_less
                    ),
                    routes = RoutesUIState(
                        onRoutes = R.string.on_routes,
                        routes = routes(item.routes),
                    )
                )
            }

            val uiState = FrequencyCommitmentUIState(
                items = items
            )

            _details.postValue(uiState)
        }
    }

    private suspend fun routes(routes: List<RouteRef>) =
        routes.map { routeRef ->
            val route = routeRepository.fromRef(routeRef)
            if (route.isSuccess) {
                val value = route.getOrNull()
                if (value != null) {
                    "${value.shortName}: ${value.longName}"
                } else {
                    routeRef.id
                }
            } else {
                routeRef.id
            }
        }

    private fun directions(item: FrequencyCommitmentItem): List<FrequencyCommitmentDirectionUIState> {
        val from = R.string.from
        val to = R.string.to
        val localizedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

        return item.directions.map { directionTime ->
            FrequencyCommitmentDirectionUIState(
                direction = direction(directionTime),
                from = from,
                startTime = directionTime.start,
                to = to,
                endTime = directionTime.end,
                timeFormatter = localizedTime
            )
        }
    }

    private fun direction(directionTime: DirectionTime): Int? {
        val direction = directionTime.direction ?: return null
        return when (direction) {
            com.mobilispect.common.data.frequency.Direction.Inbound -> R.string.inbound
            com.mobilispect.common.data.frequency.Direction.Outbound -> R.string.outbound
        }
    }
}

data class FrequencyCommitmentUIState(
    val items: Collection<FrequencyCommitmentItemUIState>,
)

data class FrequencyCommitmentItemUIState(
    @StringRes
    val daysOfTheWeek: Int,
    val directions: Collection<FrequencyCommitmentDirectionUIState>,
    val frequency: FrequencyCommitmentFrequencyUIState,
    val routes: RoutesUIState,
)

data class FrequencyCommitmentDirectionUIState(
    @StringRes
    val direction: Int?,

    @StringRes
    val from: Int,

    val startTime: LocalTime,

    @StringRes
    val to: Int,
    val endTime: LocalTime,

    val timeFormatter: DateTimeFormatter,
)

data class FrequencyCommitmentFrequencyUIState(
    @StringRes
    val every: Int,
    val frequency: Long,
    val minutesOrLess: Int,
)

data class RoutesUIState(
    val onRoutes: Int,
    val routes: Collection<String>,
)
