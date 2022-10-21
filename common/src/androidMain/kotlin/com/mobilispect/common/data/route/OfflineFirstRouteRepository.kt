package com.mobilispect.common.data.route

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class OfflineFirstRouteRepository @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val routeNetworkDataSource: RouteNetworkDataSource,
    private val routeDAO: RouteDAO
) :
    RouteRepository {
    override suspend fun fromRef(routeRef: RouteRef): Result<Route?> {
        return withContext(coroutineDispatcher) {
            val cachedRoute = routeDAO.withRef(routeRef)
            if (cachedRoute != null) {
                return@withContext Result.success(cachedRoute)
            }

            return@withContext routeNetworkDataSource.invoke(routeRef)
                .onSuccess { networkRoute ->
                    if (networkRoute != null) {
                        routeDAO.insert(networkRoute)
                    }
                }
        }
    }
}