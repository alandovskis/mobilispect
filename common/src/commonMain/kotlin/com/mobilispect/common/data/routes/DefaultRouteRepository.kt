package com.mobilispect.common.data.routes

import javax.inject.Inject

class DefaultRouteRepository @Inject constructor(private val transitLandDataSource: TransitLandRouteDataSource,
                                                 private val routeDAO: RouteDAO
) :
    RouteRepository {
    override suspend fun fromRef(routeRef: RouteRef): Result<Route?> {
        val cachedRoute = routeDAO.withRef(routeRef)
        if (cachedRoute != null) {
            return Result.success(cachedRoute)
        }

        return transitLandDataSource.invoke(routeRef)
            .onSuccess { networkRoute ->
                if (networkRoute != null) {
                    routeDAO.insert(networkRoute)
                }
            }
    }
}