package com.mobilispect.android.ui.agencies

import com.google.common.truth.Truth.assertThat
import com.mobilispect.android.testing.MainDispatcherRule
import com.mobilispect.common.data.agency.Agency
import com.mobilispect.common.data.agency.AgencyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val AGENCY_A = Agency(
    id = "o-abcd-a",
    name = "Agency A"
)
private val AGENCY_B = Agency(
    id = "o-abcd-b",
    name = "Agency B"
)

@OptIn(ExperimentalCoroutinesApi::class)
class AgenciesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var agencyRepository: TestAgencyRepository

    private lateinit var subject: AgenciesViewModel

    @Before
    fun prepare() {
        agencyRepository = TestAgencyRepository()
        subject = AgenciesViewModel(
            agencyRepository = agencyRepository
        )
    }

    @Test
    fun syncedUponCreation() = runTest {
        val uiState = subject.uiState.first()

        assertThat(uiState).isInstanceOf(AgenciesFound::class.java)
        val actualAgencies = (uiState as AgenciesFound).agencies.map { agency -> agency.id }
        assertThat(actualAgencies).containsExactly(AGENCY_A.id, AGENCY_B.id)
            .inOrder()
    }

    class TestAgencyRepository : AgencyRepository {
        private val agencies = mutableListOf<Agency>()

        override fun all(): Flow<List<Agency>> = flowOf(agencies)

        override suspend fun sync() {
            agencies.add(AGENCY_B)
            agencies.add(AGENCY_A)
        }
    }
}
