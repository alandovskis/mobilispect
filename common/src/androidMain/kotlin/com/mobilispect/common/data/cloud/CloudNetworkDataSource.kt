package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.Agency
import com.mobilispect.common.data.agency.AgencyRef
import com.mobilispect.common.data.agency.STM_ID
import com.mobilispect.common.data.route.Route
import javax.inject.Inject

private val STM = Agency(
    ref = STM_ID,
    name = "Société de transport de Montréal"
)

class CloudNetworkDataSource @Inject constructor() : NetworkDataSource {
    override suspend fun agencies(): Collection<Agency> = listOf(STM)
    override suspend fun routes(agency: AgencyRef): Collection<Route> {
        TODO("Not yet implemented")
    }
}