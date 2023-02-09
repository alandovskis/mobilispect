package com.mobilispect.common.data.route

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeRouteDAO : RouteDAO {
    private val routes = mutableListOf<Route>()
    private val routesByAgency = mutableMapOf<String, MutableCollection<Route>>()

    override suspend fun insert(route: Route) {
        routes.add(route)
        routesByAgency.putIfAbsent(route.agencyID, mutableListOf())
        routesByAgency[route.agencyID]!!.add(route)
    }

    override suspend fun delete(route: Route) {
        routes.remove(route)
    }

    override fun all(): Flow<List<Route>> = flowOf(routes)

    override fun operatedBy(agencyID: String): Flow<List<Route>> =
        flowOf(routesByAgency[agencyID]?.toList() ?: emptyList())
}