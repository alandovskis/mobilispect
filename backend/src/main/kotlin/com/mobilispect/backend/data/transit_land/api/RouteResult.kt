package com.mobilispect.backend.data.transit_land.api

/**
 * The combination of routes extracted and any paging parameters.
 */
class RouteResult(val routes: Collection<RouteResultItem>, val after: Int)
