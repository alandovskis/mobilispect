package com.mobilispect.android.data.routes

import androidx.collection.LruCache
import com.mobilispect.common.data.routes.Route
import com.mobilispect.common.data.routes.RouteRef
import com.mobilispect.common.data.routes.TransitLandRouteDataSource
import javax.inject.Inject

interface RouteRepository {
    suspend fun fromRef(routeRef: RouteRef): Route?
}

class DefaultRouteRepository @Inject constructor(private val transitLandDataSource: TransitLandRouteDataSource) : RouteRepository {
    private val routesCache: LruCache<RouteRef, Route> = LruCache(20)

    override suspend fun fromRef(routeRef: RouteRef): Route? {
        val cachedRoute = routesCache[routeRef]
        if (cachedRoute != null) {
            return cachedRoute
        }

        val route = transitLandDataSource.fromRef(routeRef)
        if (route != null) {
            routesCache.put(routeRef, route)
        }

        return route
    }
}