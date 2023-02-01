package com.mobilispect.common.data.cloud

import javax.inject.Inject

private val STM = NetworkAgency(
    name = "Société de transport de Montréal",
    _links = Links(self = Link(href = ""))
)

class CloudNetworkDataSource @Inject constructor() : NetworkDataSource {
    override suspend fun agencies(): Collection<NetworkAgency> = listOf(STM)
}