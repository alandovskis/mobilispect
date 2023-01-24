package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.AgencyRef
import javax.inject.Inject

private val AGENCY_A = NetworkAgency(
    ref = AgencyRef("abcd", "a"),
    name = "Agency A"
)
private val AGENCY_B = NetworkAgency(
    ref = AgencyRef("abcd", "b"),
    name = "Agency B"
)

/**
 * A [NetworkDataSource] that can be driven by tests.
 */
class FakeNetworkDataSource @Inject constructor() : NetworkDataSource {
    private var agencies = mutableListOf(AGENCY_A, AGENCY_B)

    override suspend fun agencies(): Collection<NetworkAgency> = agencies
}
