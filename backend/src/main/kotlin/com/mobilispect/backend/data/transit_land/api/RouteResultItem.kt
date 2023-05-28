package com.mobilispect.backend.data.transit_land.api

import com.mobilispect.backend.data.route.FeedLocalRouteID
import com.mobilispect.backend.data.route.OneStopRouteID

data class RouteResultItem(val id: OneStopRouteID, val agencyID: String, val routeID: FeedLocalRouteID)
