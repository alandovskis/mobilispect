@file:Suppress("PackageNaming")

package com.mobilispect.android.ui.frequency_commitment

import MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.frequency_commitment.FrequencyCommitment
import com.mobilispect.common.data.frequency_commitment.FrequencyCommitmentItem
import com.mobilispect.common.data.frequency_commitment.TestFrequencyCommitmentRepository
import com.mobilispect.common.data.route.Route
import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.route.TestRouteRepository
import com.mobilispect.common.data.time.WEEKDAYS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Duration
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class FrequencyCommitmentViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val agencyRef: AgencyRef = AgencyRef(geohash = "abcd", "ABCD")

    private val routeRepository = TestRouteRepository()
    private val frequencyCommitmentRepository = TestFrequencyCommitmentRepository()
    private lateinit var subject: FrequencyCommitmentViewModel

    @Before
    fun setup() {
        subject = FrequencyCommitmentViewModel(routeRepository, frequencyCommitmentRepository)
    }

    @Test
    fun noCommitmentFound() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) {
            subject.uiState(agencyRef).collect { uiState ->
                assertThat(uiState).isEqualTo(NoCommitmentFound)
            }
        }

        collectJob.cancel()
    }

    @Test
    fun commitmentFound_bothDirections_routeNotFound() = runTest {
        val agency = agencyRef
        val expectedDirections =
            com.mobilispect.common.data.frequency_commitment.DirectionTime.both(
                start = LocalTime.of(6, 0),
                end = LocalTime.of(21, 0)
            )
        val commitmentItem = FrequencyCommitmentItem(
            daysOfWeek = WEEKDAYS, frequency = Duration.ofMinutes(10),
            directions = expectedDirections,
            routes = listOf(RouteRef("not", "found"))
        )
        val commitment = FrequencyCommitment(
            spans = listOf(
                commitmentItem
            ),
            agency = agency
        )
        frequencyCommitmentRepository.insert(commitment)

        val collectJob = launch(UnconfinedTestDispatcher()) {
            subject.uiState(agency).collect { uiState ->
                val ui = uiState as CommitmentFound
                assertThat(ui.items).hasSize(1)

                val uiItem = ui.items.first()

                assertThat(uiItem.directions).hasSize(1)
                val actualDirection = uiItem.directions.first()
                val expectedDirection = expectedDirections.first()
                assertThat(actualDirection.startTime).isEqualTo(expectedDirection.start)
                assertThat(actualDirection.endTime).isEqualTo(expectedDirection.end)
                assertThat(actualDirection.isBothDirections).isEqualTo(true)

                assertThat(uiItem.frequency).isEqualTo(10)
                assertThat(uiItem.daysOfTheWeek).isEqualTo(commitmentItem.daysOfWeek)
                assertThat(uiItem.routes).isEmpty()
            }
        }

        collectJob.cancel()
    }

    @Test
    fun commitmentFound_bothDirections_routeFound() = runTest {
        val route = Route(
            id = RouteRef("was", "found"),
            shortName = "Found",
            longName = "Really Found"
        )
        routeRepository.insert(route)

        val agency = agencyRef
        val expectedDirections =
            com.mobilispect.common.data.frequency_commitment.DirectionTime.both(
                start = LocalTime.of(6, 0),
                end = LocalTime.of(21, 0)
            )
        val commitmentItem = FrequencyCommitmentItem(
            daysOfWeek = WEEKDAYS, frequency = Duration.ofMinutes(10),
            directions = expectedDirections,
            routes = listOf(route.id)
        )
        val commitment = FrequencyCommitment(
            spans = listOf(
                commitmentItem
            ),
            agency = agency
        )
        frequencyCommitmentRepository.insert(commitment)

        val collectJob = launch(UnconfinedTestDispatcher()) {
            subject.uiState(agency).collect { uiState ->
                val ui = uiState as CommitmentFound
                assertThat(ui.items).hasSize(1)

                val uiItem = ui.items.first()

                assertThat(uiItem.directions).hasSize(1)
                val actualDirection = uiItem.directions.first()
                val expectedDirection = expectedDirections.first()
                assertThat(actualDirection.startTime).isEqualTo(expectedDirection.start)
                assertThat(actualDirection.endTime).isEqualTo(expectedDirection.end)
                assertThat(actualDirection.isBothDirections).isEqualTo(true)

                assertThat(uiItem.frequency).isEqualTo(10)
                assertThat(uiItem.daysOfTheWeek).isEqualTo(commitmentItem.daysOfWeek)

                assertThat(uiItem.routes).hasSize(1)
                val actualRoute = uiItem.routes.first()
                assertThat(actualRoute.route).isEqualTo("${route.shortName}: ${route.longName}")
            }
        }

        collectJob.cancel()
    }
}
