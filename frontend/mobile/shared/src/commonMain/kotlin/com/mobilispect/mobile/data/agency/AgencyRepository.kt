package com.mobilispect.mobile.data.agency

import com.mobilispect.mobile.data.agency.Agency
import kotlinx.coroutines.flow.Flow

interface AgencyRepository {
    fun all(): Flow<List<Agency>>
    suspend fun sync()
}
