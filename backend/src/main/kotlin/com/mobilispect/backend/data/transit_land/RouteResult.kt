package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.route.Route

/**
 * The combination of routes extracted and any paging parameters.
 */
class RouteResult(val routes: Collection<Route>, val after: Int)
