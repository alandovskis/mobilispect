package com.mobilispect.backend.schedule.transit_land.api

import com.mobilispect.backend.schedule.route.FeedLocalRouteID
import com.mobilispect.backend.schedule.route.OneStopRouteID

data class RouteResultItem(val id: OneStopRouteID, val agencyID: String, val routeID: FeedLocalRouteID)
