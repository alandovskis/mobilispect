package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.route.RouteResultItem

/**
 * The combination of routes extracted and any paging parameters.
 */
class RouteResult(val routes: Collection<RouteResultItem>, val after: Int)
