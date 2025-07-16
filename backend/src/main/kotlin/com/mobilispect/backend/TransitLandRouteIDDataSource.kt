package com.mobilispect.backend.schedule.transit_land

import com.mobilispect.backend.schedule.api.PagingParameters
import com.mobilispect.backend.schedule.route.RouteIDDataSource
import com.mobilispect.backend.RouteResultItem
import com.mobilispect.backend.schedule.transit_land.api.TransitLandAPI
import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository

/**
 * A [RouteIDDataSource] uses transit land for route IDs.
 */
internal class TransitLandRouteIDDataSource(
    private val transitLandAPI: TransitLandAPI,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : RouteIDDataSource {
    override fun routeIDs(feedID: String): Result<Map<String, String>> {
        return findRouteIDs(feedID)
            .map { routes ->
                routes.fold(mutableMapOf()) { acc, item ->
                    acc[item.routeID] = item.id
                    acc
                }
            }
    }

    @Suppress("ReturnCount")
    private fun findRouteIDs(feedID: String): Result<Collection<RouteResultItem>> {
        val apiKey = transitLandCredentialsRepository.get() ?: return Result.failure(Exception("Missing API key"))
        val allRoutes = mutableListOf<RouteResultItem>()
        var after: Int? = null
        do {
            val routesRes = transitLandAPI.routes(
                apiKey = apiKey,
                feedID = feedID,
                paging = PagingParameters(limit = 100, after = after)
            )
                .map {
                    after = it.after
                    return@map it.routes
                }
            if (routesRes.isFailure) {
                return routesRes
            }

            val routes = routesRes.getOrNull()!!
            allRoutes += routes

        } while (routes.isNotEmpty())
        return Result.success(allRoutes)
    }
}
