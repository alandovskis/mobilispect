package com.mobilispect.common.data.route

import kotlinx.coroutines.flow.Flow

class TestRouteRepository : RouteRepository {
    private val routesByRef: MutableMap<String, Route> = mutableMapOf()

    override suspend fun fromRef(routeRef: RouteRef): Result<Route?> {
        return Result.success(routesByRef[routeRef.id])
    }

    override suspend fun all(): Flow<Collection<Route>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncRoutesOperatedBy(agencyID: String) {
        TODO("Not yet implemented")
    }

    fun insert(route: Route) {
        routesByRef[route.id] = route
    }
}
