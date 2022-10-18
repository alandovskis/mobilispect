package com.mobilispect.android.ui.frequency_commitment

import androidx.lifecycle.ViewModel
import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.frequency_commitment.FrequencyCommitmentItem
import com.mobilispect.common.data.frequency_commitment.FrequencyCommitmentRepository
import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.route.RouteRepository
import com.mobilispect.common.data.schedule.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class FrequencyCommitmentViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val frequencyCommitmentRepository: FrequencyCommitmentRepository,
) : ViewModel() {
    fun uiState(agency: AgencyRef) =
        frequencyCommitmentRepository.forAgency(agency).map { frequencyCommitment ->
            if (frequencyCommitment == null) {
                return@map NoCommitmentFound
            }

            CommitmentFound(
                items = frequencyCommitment.items().map { item ->
                    FrequencyCommitmentItemUIState(
                        daysOfTheWeek = item.daysOfWeek,
                        directions = directions(item),
                        frequency = item.frequency.toMinutes(),
                        routes = routes(item.routes),
                    )
                }
            )
        }

    private suspend fun routes(routes: List<RouteRef>): Collection<RouteUIState> =
        routes.mapNotNull { routeRef ->
            val route = routeRepository.fromRef(routeRef)
            if (route.isSuccess) {
                val value = route.getOrNull()
                if (value != null) {
                    RouteUIState(
                        route = "${value.shortName}: ${value.longName}",
                        routeRef = routeRef
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }

    private fun directions(item: FrequencyCommitmentItem): List<FrequencyCommitmentDirectionUIState> =
        item.directions.map { directionTime ->
            FrequencyCommitmentDirectionUIState(
                direction = directionTime.direction,
                startTime = directionTime.start,
                endTime = directionTime.end,
            )
        }
}

sealed interface FrequencyCommitmentUIState
object Loading : FrequencyCommitmentUIState
object NoCommitmentFound : FrequencyCommitmentUIState
data class CommitmentFound(
    val items: Collection<FrequencyCommitmentItemUIState>,
) : FrequencyCommitmentUIState

data class FrequencyCommitmentItemUIState(
    val daysOfTheWeek: Collection<DayOfWeek>,
    val directions: Collection<FrequencyCommitmentDirectionUIState>,
    val frequency: Long,
    val routes: Collection<RouteUIState>,
)

data class FrequencyCommitmentDirectionUIState(
    val direction: Direction?,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    val isBothDirections: Boolean
        get() = com.mobilispect.common.data.frequency_commitment.DirectionTime.isBothDirections(
            direction = direction
        )
}

data class RouteUIState(
    val route: String,
    val routeRef: RouteRef,
)
