package com.mobilispect.backend.data.route

import com.mobilispect.backend.data.api.PagingParameters

interface RouteDataSource {
    fun routes(apiKey: String, agencyID: String, paging: PagingParameters = PagingParameters()): Result<Any>
}