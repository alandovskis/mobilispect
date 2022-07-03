package com.mobilispect.common.data.routes

interface RouteRepository {
    suspend fun fromRef(routeRef: RouteRef): Result<Route?>
}