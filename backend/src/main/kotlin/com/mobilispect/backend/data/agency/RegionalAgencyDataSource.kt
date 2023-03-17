package com.mobilispect.backend.data.agency

import com.mobilispect.backend.data.api.PagingParameters

interface RegionalAgencyDataSource {
    /**
     * Retrieve all [Agency] that serve a given [city].
     */
    @Suppress("ReturnCount")
    fun agencies(apiKey: String, city: String, paging: PagingParameters = PagingParameters()): Result<AgencyResult>
}
