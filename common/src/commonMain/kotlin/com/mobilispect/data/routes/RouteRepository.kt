package com.mobilispect.data.routes

interface RouteRepository {
    suspend fun fromRef(routeRef: RouteRef): Result<Route?>
}