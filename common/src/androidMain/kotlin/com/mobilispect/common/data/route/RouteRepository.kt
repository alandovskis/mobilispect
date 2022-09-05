package com.mobilispect.common.data.route

interface RouteRepository {
    suspend fun fromRef(routeRef: RouteRef): Result<Route?>
}