package com.mobilispect.common.data.route

internal class FakeRouteDAO : RouteDAO {
    private val routesByRef: MutableMap<RouteRef, Route> = mutableMapOf()

    override suspend fun insert(route: Route) {
        routesByRef[route.id] = route
    }

    override suspend fun withRef(ref: RouteRef) = routesByRef[ref]
}