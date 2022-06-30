package com.mobilispect.common.data.routes

import com.mobilispect.common.data.core.transitland.TransitLandConfigRepository
import javax.inject.Inject

/**
 * A data source that uses transit.land as its source.
 */
class TransitLandRouteDataSource @Inject constructor(
    private val transitLandAPI: TransitLandAPI,
    private val configRepository: TransitLandConfigRepository
) {
    suspend fun fromRef(routeRef: RouteRef): Route? {
        val config = configRepository.config() ?: return null
        val response = transitLandAPI.fromRef(routeRef.id, config.apiKey)
        if (response.isSuccessful) {
            val cloudRoute = response.body() ?: return null
            if (cloudRoute.routes.isEmpty()) {
                return null
            }

            val route = cloudRoute.routes.first()
            return Route(
                id = routeRef,
                shortName = route.shortName,
                longName = route.longName
            )
        }

        return null
    }
}