package com.mobilispect.common.data.route

import kotlinx.coroutines.flow.Flow

class TestRouteRepository : RouteRepository {
    private val routesByRef: MutableMap<String, Route> = mutableMapOf()

    override fun all(): Flow<Collection<Route>> {
        TODO("Not yet implemented")
    }

    override fun operatedBy(agencyID: String): Flow<Collection<Route>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncRoutesOperatedBy(agencyID: String) {
        TODO("Not yet implemented")
    }

    fun insert(route: Route) {
        routesByRef[route.id] = route
    }
}
