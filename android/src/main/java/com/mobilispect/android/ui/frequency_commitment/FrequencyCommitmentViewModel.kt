package com.mobilispect.android.ui.frequency_violation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.mobilispect.android.R
import com.mobilispect.data.frequency.Direction
import com.mobilispect.data.routes.RouteRepository
import com.mobilispect.data.frequency.DirectionTime
import com.mobilispect.data.frequency.FrequencyCommitmentItem
import com.mobilispect.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.data.routes.RouteRef
import com.mobilispect.data.time.WEEKDAYS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject


@HiltViewModel
class FrequencyCommitmentViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
) : ViewModel() {
    val details: Flow<FrequencyCommitmentUIState> = flow {
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

        emit(uiState)
    }

    private suspend fun routes(routes: List<RouteRef>): Collection<RouteUIState> =
        routes.map { routeRef ->
            val route = routeRepository.fromRef(routeRef)
            if (route.isSuccess) {
                val value = route.getOrNull()
                if (value != null) {
                    RouteUIState(
                        route = "${value.shortName}: ${value.longName}",
                        routeRef = routeRef)
                } else {
                    RouteUIState(
                        route = routeRef.routeNumber,
                        routeRef = routeRef
                    )
                }
            } else {
                RouteUIState(
                    route = routeRef.routeNumber,
                    routeRef = routeRef
                )
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
            Direction.Inbound -> R.string.inbound
            Direction.Outbound -> R.string.outbound
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
    val from: Int = R.string.from,
    val startTime: LocalTime,

    @StringRes
    val to: Int = R.string.to,
    val endTime: LocalTime,

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT),
)

data class FrequencyCommitmentFrequencyUIState(
    @StringRes
    val every: Int = R.string.every,
    val frequency: Long,
    val minutesOrLess: Int = R.string.minutes_or_less,
)

data class RoutesUIState(
    val onRoutes: Int,
    val routes: Collection<RouteUIState>,
)

data class RouteUIState(
    val route: String,
    val routeRef: RouteRef,
)
