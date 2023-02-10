package com.mobilispect.common.data.route

import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun all(): Flow<Collection<Route>>
    fun operatedBy(agencyID: String): Flow<Collection<Route>>
    suspend fun syncRoutesOperatedBy(agencyID: String)
}