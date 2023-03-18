package com.mobilispect.backend.data.route

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.transit_land.RouteResult

interface RouteDataSource {
    fun routes(apiKey: String, agencyID: String, paging: PagingParameters = PagingParameters()): Result<RouteResult>
}