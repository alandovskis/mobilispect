package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.STM_ID
import javax.inject.Inject

private val STM = NetworkAgency(
    ref = STM_ID,
    name = "Société de transport de Montréal"
)

class CloudNetworkDataSource @Inject constructor() : NetworkDataSource {
    override suspend fun agencies(): Collection<NetworkAgency> = listOf(STM)
}