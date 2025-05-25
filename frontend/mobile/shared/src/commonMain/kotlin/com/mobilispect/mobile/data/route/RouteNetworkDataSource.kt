package com.mobilispect.common.data.route

interface RouteNetworkDataSource {
    suspend operator fun invoke(routeRef: RouteRef): Result<Route?>
}