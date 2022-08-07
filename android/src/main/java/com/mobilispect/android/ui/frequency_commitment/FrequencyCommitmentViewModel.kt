package com.mobilispect.android.ui.frequency_commitment

import androidx.lifecycle.ViewModel
import com.mobilispect.data.frequency.Direction
import com.mobilispect.data.routes.RouteRepository
import com.mobilispect.data.frequency.DirectionTime
import com.mobilispect.data.frequency.FrequencyCommitmentItem
import com.mobilispect.data.frequency.STM_FREQUENCY_COMMITMENT
import com.mobilispect.data.routes.RouteRef
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class FrequencyCommitmentViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
) : ViewModel() {
    val details: Flow<FrequencyCommitmentUIState> = flow {
        val frequencyCommitment = STM_FREQUENCY_COMMITMENT
        val items = frequencyCommitment.items().map { item ->
            FrequencyCommitmentItemUIState(
                daysOfTheWeek = item.daysOfWeek,
                directions = directions(item),
                frequency = FrequencyCommitmentFrequencyUIState(
                    frequency = item.frequency.toMinutes(),
                ),
                routes = routes(item.routes),
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
        return item.directions.map { directionTime ->
            FrequencyCommitmentDirectionUIState(
                direction = directionTime.direction,
                startTime = directionTime.start,
                endTime = directionTime.end,
            )
        }
    }
}

data class FrequencyCommitmentUIState(
    val items: Collection<FrequencyCommitmentItemUIState>,
)

data class FrequencyCommitmentItemUIState(
    val daysOfTheWeek: Collection<DayOfWeek>,
    val directions: Collection<FrequencyCommitmentDirectionUIState>,
    val frequency: FrequencyCommitmentFrequencyUIState,
    val routes: Collection<RouteUIState>,
)

data class FrequencyCommitmentDirectionUIState(
    val direction: Direction?,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    val isBothDirections: Boolean
        get() = DirectionTime.isBothDirections(direction = direction)
}

data class FrequencyCommitmentFrequencyUIState(
    val frequency: Long,
)

data class RouteUIState(
    val route: String,
    val routeRef: RouteRef,
)
