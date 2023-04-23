package com.mobilispect.backend.data.route

/**
 * A map to look up a route onestop ID given a routeID.
 * RouteID must be unique within a feed (https://gtfs.org/schedule/reference/#routestxt)
 */
class RouteIDMap(private val store: MutableMap<FeedLocalRouteID, OneStopRouteID> = mutableMapOf()) {
    fun add(routeID: FeedLocalRouteID, onestopID: OneStopRouteID) {
        store[routeID] = onestopID
    }

    fun get(routeID: FeedLocalRouteID): OneStopRouteID? = store[routeID]
}
