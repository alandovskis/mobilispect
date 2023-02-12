package com.mobilispect.android.ui.routes

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.route.Route
import com.mobilispect.common.data.route.TestRouteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

private val ROUTE_A1 =
    Route(id = "r-abcd-1", shortName = "1", longName = "Main Street", agencyID = "o-abcd-a")
private val ROUTE_B3 =
    Route(id = "r-cdef-3", shortName = "3", longName = "1st Street", agencyID = "o-cdef-b")

@OptIn(ExperimentalCoroutinesApi::class)
@Ignore
class RoutesListViewModelTest {
    private lateinit var routeRepository: TestRouteRepository

    private lateinit var subject: RoutesListViewModel

    @Before
    fun prepare() {
        val savedStateHandle = SavedStateHandle(mapOf("agencyID" to ROUTE_A1.agencyID))
        routeRepository = TestRouteRepository()
        subject = RoutesListViewModel(savedStateHandle, routeRepository)
    }

    @Test
    fun uiStateBackedByRouteRepository() = runTest {
        routeRepository.insert(ROUTE_A1)
        routeRepository.insert(ROUTE_B3)

        val collectJob = launch(UnconfinedTestDispatcher()) {
            subject.uiState.collect { actual ->
                assertThat(actual).isInstanceOf(RoutesFound::class.java)
                assertThat((actual as RoutesFound).routes).containsExactly(
                    RouteUIState(
                        id = ROUTE_A1.id,
                        shortName = ROUTE_A1.shortName,
                        longName = ROUTE_A1.longName,
                        agencyID = ROUTE_A1.agencyID
                    )
                )
            }
        }

        collectJob.cancel()
    }
}
