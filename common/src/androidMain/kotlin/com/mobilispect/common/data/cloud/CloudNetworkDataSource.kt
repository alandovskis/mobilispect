package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.STM_ID
import javax.inject.Inject

private val STM = NetworkAgency(
    name = "Société de transport de Montréal",
    _links = Links(self = Link(href = "http://localhost:8080/$STM_ID"))
)

class CloudNetworkDataSource @Inject constructor() : NetworkDataSource {
    override suspend fun agencies(): Collection<NetworkAgency> = listOf(STM)
}