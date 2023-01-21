package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.Agency
import com.mobilispect.common.data.agency.AgencyRef
import javax.inject.Inject

private val AGENCY_A = Agency(
    ref = AgencyRef("abcd", "a"),
    name = "Agency A"
)
private val AGENCY_B = Agency(
    ref = AgencyRef("abcd", "b"),
    name = "Agency B"
)

/**
 * A [NetworkDataSource] that can be driven by tests.
 */
class FakeNetworkDataSource @Inject constructor() : NetworkDataSource {
    private val agencies = listOf(AGENCY_A, AGENCY_B)

    override suspend fun agencies(): Collection<Agency> = agencies
}
