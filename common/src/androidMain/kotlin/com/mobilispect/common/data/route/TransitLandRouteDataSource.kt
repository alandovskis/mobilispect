package com.mobilispect.common.data.route

import com.mobilispect.common.data.transit_land.TransitLandClient
import javax.inject.Inject

/**
 * A data source that uses transit.land as its source.
 */
internal class TransitLandRouteDataSource @Inject constructor(
    private val transitLandAPI: TransitLandClient,
) : RouteNetworkDataSource {
    override suspend operator fun invoke(routeRef: RouteRef): Result<Route?> =
        transitLandAPI.fromRef(routeRef.id)
            .map { cloudRoute ->
                if (cloudRoute.routes.isEmpty()) {
                    return@map null
                }

                val route = cloudRoute.routes.first()
                return@map Route(
                    id = routeRef,
                    shortName = route.shortName,
                    longName = route.longName
                )
            }
}