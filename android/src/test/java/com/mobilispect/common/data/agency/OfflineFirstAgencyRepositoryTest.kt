package com.mobilispect.common.data.agency

import com.google.common.truth.Truth.assertThat
import com.mobilispect.common.data.cloud.NetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

private val AGENCY_A = Agency(
    ref = AgencyRef("abcd", "a"),
    name = "Agency A"
)
private val AGENCY_B = Agency(
    ref = AgencyRef("abcd", "b"),
    name = "Agency B"
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
            networkDataSource = networkDataSource
        )
    }

    @Test
    fun syncAddsAgenciesWhenNoneFound() = runTest {
        networkDataSource.insert(AGENCY_A)
        networkDataSource.insert(AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(AGENCY_A, AGENCY_B)
    }

    @Test
    fun syncAddsMissingAgencyWhenOneIsMissing() = runTest {
        networkDataSource.insert(AGENCY_A)
        networkDataSource.insert(AGENCY_B)
        agencyDAO.insert(AGENCY_A)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(AGENCY_A, AGENCY_B)
    }

    @Test
    fun syncChangesNothingWhenAllPresent() = runTest {
        networkDataSource.insert(AGENCY_A)
        networkDataSource.insert(AGENCY_B)
        agencyDAO.insert(AGENCY_A)
        agencyDAO.insert(AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(AGENCY_A, AGENCY_B)
    }

    @Test
    fun syncDeletesIfNoLongerFoundInNetworkDataSource() = runTest {
        networkDataSource.insert(AGENCY_B)
        agencyDAO.insert(AGENCY_A)
        agencyDAO.insert(AGENCY_B)

        subject.sync()

        val actual = subject.all().first()
        assertThat(actual).containsExactly(AGENCY_B)
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
        private val agencies = mutableListOf<Agency>()

        override suspend fun agencies(): Collection<Agency> = agencies

        fun insert(agency: Agency) {
            agencies.add(agency)
        }
    }
}