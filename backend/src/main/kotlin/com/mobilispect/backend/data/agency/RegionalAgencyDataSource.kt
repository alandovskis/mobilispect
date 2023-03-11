package com.mobilispect.backend.data.agency

interface RegionalAgencyDataSource {
    /**
     * Retrieve all [Agency] that serve a given [city].
     */
    @Suppress("ReturnCount")
    fun agencies(apiKey: String, city: String, limit: Int = 20, after: Int? = null): Result<AgencyResult>
}