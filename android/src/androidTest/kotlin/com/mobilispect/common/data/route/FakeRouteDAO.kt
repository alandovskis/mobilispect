package com.mobilispect.common.data.route

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeRouteDAO : RouteDAO {
    private val routes = mutableListOf<Route>()

    override suspend fun insert(route: Route) {
        routes.add(route)
    }

    override suspend fun delete(route: Route) {
        routes.remove(route)
    }

    override suspend fun withRef(ref: RouteRef) = routes.find { route -> route.id == ref.id }
    override fun all(): Flow<List<Route>> = flowOf(routes)
}