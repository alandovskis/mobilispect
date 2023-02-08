package com.mobilispect.common.data.cloud

class TestNetworkDataSource : NetworkDataSource {
    private val agencies = mutableListOf<NetworkAgency>()
    private val routesByAgency = mutableMapOf<String, MutableCollection<NetworkRoute>>()

    override suspend fun agencies(): Result<Collection<NetworkAgency>> =
        Result.success(agencies)

    override suspend fun routesOperatedBy(agencyID: String): Result<Collection<NetworkRoute>> =
        Result.success(routesByAgency[agencyID] ?: emptyList())

    fun insert(agency: NetworkAgency) {
        agencies.add(agency)
    }

    fun insert(route: NetworkRoute) {
        routesByAgency.putIfAbsent(route.agencyID, mutableListOf())
        routesByAgency[route.agencyID]!!.add(route)
    }
}