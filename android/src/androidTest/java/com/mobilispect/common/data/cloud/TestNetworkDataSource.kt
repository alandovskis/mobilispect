package com.mobilispect.common.data.cloud

class TestNetworkDataSource : NetworkDataSource {
    private val agencies = mutableListOf<NetworkAgency>()

    override suspend fun agencies(): Result<Collection<NetworkAgency>> =
        Result.success(agencies)

    fun insert(agency: NetworkAgency) {
        agencies.add(agency)
    }
}