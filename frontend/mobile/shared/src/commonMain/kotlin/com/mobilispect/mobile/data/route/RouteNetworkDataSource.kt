package com.mobilispect.common.data.route

import com.mobilispect.mobile.data.route.Route
import com.mobilispect.mobile.data.route.RouteRef

interface RouteNetworkDataSource {
    suspend operator fun invoke(routeRef: RouteRef): Result<Route?>
}