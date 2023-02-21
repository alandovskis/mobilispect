package com.mobilispect.common.data.route

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.AppDatabase
import com.mobilispect.common.data.cloud.Link
import com.mobilispect.common.data.cloud.Links
import com.mobilispect.common.data.cloud.NetworkRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val AGENCY_A = "o-abcd-a"
private const val AGENCY_B = "o-cdef-a"
private val NETWORK_ROUTE_A1 = NetworkRoute(
    shortName = "1",
    longName = "Main Street", agencyID = AGENCY_A,
    _links = Links(Link("http://localhost:64123/routes/r-abcd-1"))
)
private val NETWORK_ROUTE_A2 = NetworkRoute(
    shortName = "2",
    longName = "Central Avenue", agencyID = AGENCY_A,
    _links = Links(Link("http://localhost:64123/routes/r-abcd-2"))
)

private val LOCAL_ROUTE_A1 = Route(
    id = "r-abcd-1",
    shortName = NETWORK_ROUTE_A1.shortName,
    longName = NETWORK_ROUTE_A1.longName,
    agencyID = NETWORK_ROUTE_A1.agencyID
)
private val LOCAL_ROUTE_A2 = Route(
    id = "r-abcd-2",
    shortName = NETWORK_ROUTE_A2.shortName,
    longName = NETWORK_ROUTE_A2.longName,
    agencyID = NETWORK_ROUTE_A2.agencyID
)
private val LOCAL_ROUTE_B3 = Route(
    id = "r-abcd-3",
    shortName = "3",
    longName = "1st Avenue",
    agencyID = AGENCY_B
)

@OptIn(ExperimentalCoroutinesApi::class)
class RouteDAOTest {
    private lateinit var appDatabase: AppDatabase

    private lateinit var subject: RouteDAO

    @Before
    fun setup() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .build()
        subject = appDatabase.routeDAO()
    }

    @Test
    fun operatedByOnlyReturnsRoutesRunBySpecifiedAgency() = runTest {
        subject.insert(LOCAL_ROUTE_A1)
        subject.insert(LOCAL_ROUTE_A2)
        subject.insert(LOCAL_ROUTE_B3)

        val actual = subject.operatedBy(AGENCY_A).first()

        assertThat(actual).containsExactly(LOCAL_ROUTE_A1, LOCAL_ROUTE_A2)
    }

    @Test
    fun allReturnsAllInsertedRoutes() = runTest {
        subject.insert(LOCAL_ROUTE_A1)
        subject.insert(LOCAL_ROUTE_A2)
        subject.insert(LOCAL_ROUTE_B3)

        val actual = subject.all().first()

        assertThat(actual).containsExactly(LOCAL_ROUTE_A1, LOCAL_ROUTE_A2, LOCAL_ROUTE_B3)
    }

    @Test
    fun deleteRemovesOnlyTheSpecifiedRoute() = runTest {
        subject.insert(LOCAL_ROUTE_A1)
        subject.insert(LOCAL_ROUTE_A2)

        subject.delete(LOCAL_ROUTE_A2)

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_ROUTE_A1)
    }

    @After
    fun clear() {
        appDatabase.clearAllTables()
    }
}