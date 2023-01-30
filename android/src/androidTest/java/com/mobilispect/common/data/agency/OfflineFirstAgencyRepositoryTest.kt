package com.mobilispect.common.data.agency

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.AppDatabase
import com.mobilispect.common.data.cloud.NetworkAgency
import com.mobilispect.common.data.cloud.NetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

private val NETWORK_AGENCY_A = NetworkAgency(
    ref = AgencyRef("abcd", "a"),
    name = "Agency A"
)
private val NETWORK_AGENCY_B = NetworkAgency(
    ref = AgencyRef("abcd", "b"),
    name = "Agency B"
)
private val LOCAL_AGENCY_A = Agency(
    ref = NETWORK_AGENCY_A.ref,
    name = NETWORK_AGENCY_A.name
)
private val LOCAL_AGENCY_B = Agency(
    ref = NETWORK_AGENCY_B.ref,
    name = NETWORK_AGENCY_B.name
)

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstAgencyRepositoryTest {
    private lateinit var agencyDAO: TestAgencyDAO
    private lateinit var networkDataSource: TestNetworkDataSource

    private lateinit var subject: OfflineFirstAgencyRepository

    @Before
    fun prepare() {
        agencyDAO = TestAgencyDAO()
        networkDataSource = TestNetworkDataSource()
        subject = OfflineFirstAgencyRepository(
            agencyDAO = agencyDAO,
            networkDataSource = networkDataSource,
            appDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            )
                .build()
        )
    }

    @Test
    fun syncAddsAgenciesWhenNoneFound() = runTest {
        networkDataSource.insert(NETWORK_AGENCY_A)
        networkDataSource.insert(NETWORK_AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_AGENCY_A, LOCAL_AGENCY_B)
    }

    @Test
    fun syncAddsMissingAgencyWhenOneIsMissing() = runTest {
        networkDataSource.insert(NETWORK_AGENCY_A)
        networkDataSource.insert(NETWORK_AGENCY_B)
        agencyDAO.insert(LOCAL_AGENCY_A)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_AGENCY_A, LOCAL_AGENCY_B)
    }

    @Test
    fun syncChangesNothingWhenAllPresent() = runTest {
        networkDataSource.insert(NETWORK_AGENCY_A)
        networkDataSource.insert(NETWORK_AGENCY_B)
        agencyDAO.insert(LOCAL_AGENCY_A)
        agencyDAO.insert(LOCAL_AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_AGENCY_A, LOCAL_AGENCY_B)
    }

    @Test
    fun syncDeletesIfNoLongerFoundInNetworkDataSource() = runTest {
        networkDataSource.insert(NETWORK_AGENCY_B)
        agencyDAO.insert(LOCAL_AGENCY_A)
        agencyDAO.insert(LOCAL_AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(LOCAL_AGENCY_B)
    }

    class TestAgencyDAO : AgencyDAO {
        private val agencies = mutableListOf<Agency>()

        override suspend fun insert(agency: Agency) {
            check(!agencies.contains(agency))
            agencies.add(agency)
        }

        override suspend fun delete(agency: Agency) {
            agencies.remove(agency)
        }

        override fun all(): Flow<List<Agency>> = flowOf(agencies)
    }

    class TestNetworkDataSource : NetworkDataSource {
        private val agencies = mutableListOf<NetworkAgency>()

        override suspend fun agencies(): Collection<NetworkAgency> = agencies

        fun insert(agency: NetworkAgency) {
            agencies.add(agency)
        }
    }
}
