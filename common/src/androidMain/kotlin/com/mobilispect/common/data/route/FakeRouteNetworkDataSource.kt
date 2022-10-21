package com.mobilispect.common.data.route

internal class FakeRouteNetworkDataSource : RouteNetworkDataSource {
    private val routesByRef: MutableMap<RouteRef, Result<Route?>> = mutableMapOf()

    override suspend fun invoke(routeRef: RouteRef): Result<Route?> = routesByRef[routeRef]
        ?: throw IllegalArgumentException("No result for $routeRef")

    internal fun insertResult(ref: RouteRef, result: Result<Route?>) {
        routesByRef[ref] = result
    }
}