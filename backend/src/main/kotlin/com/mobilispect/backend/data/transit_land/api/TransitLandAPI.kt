package com.mobilispect.backend.data.transit_land.api

import com.mobilispect.backend.data.agency.AgencyResult
import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.stop.StopResult
import com.mobilispect.backend.data.transit_land.RouteResult

/**
 * A client to access the transitland API.
 */
interface TransitLandAPI {
    /**
     * Retrieve the feed identified by [feedID].
     */
    fun feed(apiKey: String, feedID: String): Result<VersionedFeed>

    /**
     * Retrieve all agencies that serve a given [region] or are contained in the feed identified by [feedID].
     */
    @Suppress("ReturnCount")
    fun agencies(apiKey: String, region: String? = null, feedID: String? = null): Result<AgencyResult>

    /**
     * Retrieve the routes contained in the feed identified by [feedID].
     */
    fun routes(apiKey: String, feedID: String, paging: PagingParameters = PagingParameters()): Result<RouteResult>

    /**
     * Retrieve all stops contained in the feed identified by [feedID].
     */
    fun stops(apiKey: String, feedID: String, paging: PagingParameters = PagingParameters()): Result<StopResult>
}