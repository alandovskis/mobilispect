package com.mobilispect.common.data.route

import androidx.room.withTransaction
import com.mobilispect.common.data.AppDatabase
import com.mobilispect.common.data.cloud.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class OfflineFirstRouteRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val routeDAO: RouteDAO,
    private val appDatabase: AppDatabase,
) :
    RouteRepository {
    override fun all(): Flow<Collection<Route>> = routeDAO.all()

    override fun operatedBy(agencyID: String): Flow<Collection<Route>> =
        routeDAO.operatedBy(agencyID)

    override suspend fun syncRoutesOperatedBy(agencyID: String) {
        val remoteRes = networkDataSource.routesOperatedBy(agencyID)
        remoteRes.onSuccess { remoteAgencies ->
            appDatabase.withTransaction {
                val local = routeDAO.all().first()
                val remote = remoteAgencies.map { route -> route.asEntity() }

                val localIDs = local.map { route -> route.id }
                val remoteIDs = remote.map { route -> route.id }

                val toAdd = remote.filterNot { route -> localIDs.contains(route.id) }
                for (route in toAdd) {
                    routeDAO.insert(route)
                }

                val toRemove = local.filterNot { route -> remoteIDs.contains(route.id) }
                for (route in toRemove) {
                    routeDAO.delete(route)
                }
            }
        }
    }
}
