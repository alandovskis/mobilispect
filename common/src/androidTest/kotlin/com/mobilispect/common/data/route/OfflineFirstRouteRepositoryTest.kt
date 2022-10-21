package com.mobilispect.common.data.route

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstRouteRepositoryTest {
    private val routeRef = RouteRef("abcd", "1")
    private val route = Route(id = routeRef, shortName = "1", "Line One")

    private val testScope = TestScope()

    private lateinit var routeDAO: FakeRouteDAO
    private lateinit var networkDataSource: FakeRouteNetworkDataSource

    private lateinit var subject: OfflineFirstRouteRepository

    @Before
    fun setup() {
        val coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        networkDataSource = FakeRouteNetworkDataSource()
        routeDAO = FakeRouteDAO()
        subject = OfflineFirstRouteRepository(
            coroutineDispatcher = coroutineDispatcher,
            routeNetworkDataSource = networkDataSource,
            routeDAO = routeDAO
        )
    }

    @Test
    fun insertsNothingWhenNetworkDataSourceFails() = testScope.runTest() {
        networkDataSource.insertResult(routeRef, Result.failure(Exception("Error")))

        val result = subject.fromRef(routeRef)

        assertThat(result.isFailure).isTrue()
        assertThat(routeDAO.withRef(routeRef)).isNull()
    }

    @Test
    fun insertsWhenRemoteButNotLocal() = testScope.runTest {
        networkDataSource.insertResult(routeRef, Result.success(route))

        val result = subject.fromRef(routeRef)

        assertThat(result.getOrNull()).isEqualTo(route)
        assertThat(routeDAO.withRef(routeRef)).isEqualTo(route)
    }

    @Test
    fun returnsButDoesNotInsertIfAlreadyFound() = testScope.runTest {
        routeDAO.insert(route)
        assertThat(routeDAO.withRef(routeRef)).isEqualTo(route)

        val result = subject.fromRef(routeRef)

        assertThat(result.getOrNull()).isEqualTo(route)
        assertThat(routeDAO.withRef(routeRef)).isEqualTo(route)
    }
}