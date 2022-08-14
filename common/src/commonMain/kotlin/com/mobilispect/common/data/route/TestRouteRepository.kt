package com.mobilispect.common.data.route

class TestRouteRepository : RouteRepository {
    private val routesByRef: MutableMap<RouteRef, Route> = mutableMapOf()

    override suspend fun fromRef(routeRef: RouteRef): Result<Route?> {
        return Result.success(routesByRef[routeRef])
    }

    fun insert(route: Route) {
        routesByRef[route.id] = route
    }
}
