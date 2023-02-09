package com.mobilispect.common.data.route

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.AppDatabase
import com.mobilispect.common.data.cloud.Link
import com.mobilispect.common.data.cloud.Links
import com.mobilispect.common.data.cloud.NetworkRoute
import com.mobilispect.common.data.cloud.TestNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

private const val AGENCY_ID = "o-abcd-a"
private val NETWORK_ROUTE_1 = NetworkRoute(
    shortName = "1",
    longName = "Main Street", agencyID = AGENCY_ID,
    _links = Links(Link("http://localhost:64123/routes/r-abcd-1"))
)
private val NETWORK_ROUTE_2 = NetworkRoute(
    shortName = "2",
    longName = "Central Avenue", agencyID = AGENCY_ID,
    _links = Links(Link("http://localhost:64123/routes/r-abcd-2"))
)

private val LOCAL_ROUTE_1 = Route(
    id = "r-abcd-1",
    shortName = NETWORK_ROUTE_1.shortName,
    longName = NETWORK_ROUTE_1.longName,
    agencyID = NETWORK_ROUTE_1.agencyID
)
private val LOCAL_ROUTE_2 = Route(
    id = "r-abcd-2",
    shortName = NETWORK_ROUTE_2.shortName,
    longName = NETWORK_ROUTE_2.longName,
    agencyID = NETWORK_ROUTE_2.agencyID
)

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstRouteRepositoryTest {
    private val testScope = TestScope()

    private lateinit var routeDAO: FakeRouteDAO
    private lateinit var routeNetworkDataSource: RouteNetworkDataSource
    private lateinit var networkDataSource: TestNetworkDataSource

    private lateinit var subject: OfflineFirstRouteRepository

    @Before
    fun setup() {
        val coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        routeNetworkDataSource = FakeRouteNetworkDataSource()
        routeDAO = FakeRouteDAO()
        networkDataSource = TestNetworkDataSource()
        subject = OfflineFirstRouteRepository(
            coroutineDispatcher = coroutineDispatcher,
            routeNetworkDataSource = routeNetworkDataSource,
            routeDAO = routeDAO,
            networkDataSource = networkDataSource,
            appDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext<Context>(),
                AppDatabase::class.java
            )
                .build()

        )
    }

    @Test
    fun syncAddsRoutesWhenNoneFound() = runTest {
        networkDataSource.insert(NETWORK_ROUTE_1)
        networkDataSource.insert(NETWORK_ROUTE_2)

        subject.syncRoutesOperatedBy(AGENCY_ID)

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_ROUTE_1, LOCAL_ROUTE_2)
    }

    @Test
    fun syncAddsMissingAgencyWhenOneIsMissing() = runTest {
        networkDataSource.insert(NETWORK_ROUTE_1)
        networkDataSource.insert(NETWORK_ROUTE_2)
        routeDAO.insert(LOCAL_ROUTE_1)

        subject.syncRoutesOperatedBy(AGENCY_ID)

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_ROUTE_1, LOCAL_ROUTE_2)
    }

    @Test
    fun syncChangesNothingWhenAllPresent() = runTest {
        networkDataSource.insert(NETWORK_ROUTE_1)
        networkDataSource.insert(NETWORK_ROUTE_2)
        routeDAO.insert(LOCAL_ROUTE_1)
        routeDAO.insert(LOCAL_ROUTE_2)

        subject.syncRoutesOperatedBy(AGENCY_ID)

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_ROUTE_1, LOCAL_ROUTE_2)
    }

    @Test
    fun syncDeletesIfNoLongerFoundInNetworkDataSource() = runTest {
        networkDataSource.insert(NETWORK_ROUTE_2)
        routeDAO.insert(LOCAL_ROUTE_1)
        routeDAO.insert(LOCAL_ROUTE_2)

        subject.syncRoutesOperatedBy(AGENCY_ID)

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_ROUTE_2)
    }
}