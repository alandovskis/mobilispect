package com.mobilispect.common.data.cloud

import javax.inject.Inject

private val AGENCY_A = NetworkAgency(
    name = "Agency A",
    _links = Links(self = Link("o-abcd-a")),
)
private val AGENCY_B = NetworkAgency(
    name = "Agency B",
    _links = Links(self = Link("o-abcd-b")),
)

/**
 * A [NetworkDataSource] that can be driven by tests.
 */
class FakeNetworkDataSource @Inject constructor() : NetworkDataSource {
    private var agencies = mutableListOf(AGENCY_A, AGENCY_B)

    override suspend fun agencies(): Collection<NetworkAgency> = agencies
}
