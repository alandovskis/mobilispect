package com.mobilispect.common.data.agency

import androidx.room.withTransaction
import com.mobilispect.common.data.AppDatabase
import com.mobilispect.common.data.cloud.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OfflineFirstAgencyRepository @Inject constructor(
    private val agencyDAO: AgencyDAO,
    private val networkDataSource: NetworkDataSource,
    private val appDatabase: AppDatabase,
) : AgencyRepository {
    override fun all(): Flow<List<Agency>> = agencyDAO.all()

    override suspend fun sync() {
        val remoteRes = networkDataSource.agencies()
        remoteRes.onSuccess { remoteAgencies ->
            appDatabase.withTransaction {
                val local = agencyDAO.all().first()
                val remote = remoteAgencies.map { agency -> agency.asEntity() }

                val localIDs = local.map { agency -> agency.ref }
                val remoteIDs = remote.map { agency -> agency.ref }

                val toAdd = remote.filterNot { agency -> localIDs.contains(agency.ref) }
                for (agency in toAdd) {
                    agencyDAO.insert(agency)
                }

                val toRemove = local.filterNot { agency -> remoteIDs.contains(agency.ref) }
                for (agency in toRemove) {
                    agencyDAO.delete(agency)
                }
            }
        }
    }
}
