package com.mobilispect.common.data.route

class TestRouteRepository : RouteRepository {
    private val routesByRef: MutableMap<RouteRef, com.mobilispect.common.data.route.Route> = mutableMapOf()

    override suspend fun fromRef(routeRef: RouteRef): Result<com.mobilispect.common.data.route.Route?> {
        return Result.success(routesByRef[routeRef])
    }

    fun insert(route: com.mobilispect.common.data.route.Route) {
        routesByRef[route.id] = route
    }
}
