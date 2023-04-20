package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.route.OneStopRouteIDDataSource
import com.mobilispect.backend.data.route.RouteIDMap
import com.mobilispect.backend.data.route.RouteResultItem

/**
 * A [OneStopRouteIDDataSource] uses transit land for route IDs.
 */
internal class TransitLandOneStopRouteIDDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : OneStopRouteIDDataSource {
    override fun routeIDs(feedID: String): Result<RouteIDMap> {
        return findRouteIDs(feedID)
            .map { routes ->
                routes.fold(RouteIDMap()) { acc, item ->
                    acc.add(item.agencyID, item.routeID, item.id)
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
            val routesRes = transitLandClient.routes(
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
