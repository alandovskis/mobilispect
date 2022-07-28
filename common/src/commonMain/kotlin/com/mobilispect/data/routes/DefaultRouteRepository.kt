package com.mobilispect.data.routes

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultRouteRepository @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val transitLandDataSource: TransitLandRouteDataSource,
    private val routeDAO: RouteDAO
) :
    RouteRepository {
    override suspend fun fromRef(routeRef: RouteRef): Result<Route?> {
        return withContext(coroutineDispatcher) {
            val cachedRoute = routeDAO.withRef(routeRef)
            if (cachedRoute != null) {
                return@withContext Result.success(cachedRoute)
            }

            return@withContext transitLandDataSource.invoke(routeRef)
                .onSuccess { networkRoute ->
                    if (networkRoute != null) {
                        routeDAO.insert(networkRoute)
                    }
                }
        }
    }
}