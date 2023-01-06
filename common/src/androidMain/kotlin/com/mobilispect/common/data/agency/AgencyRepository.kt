package com.mobilispect.common.data.agency

import kotlinx.coroutines.flow.Flow

interface AgencyRepository {
    fun all(): Flow<List<Agency>>
    suspend fun sync()
}
