package com.mobilispect.backend.data.route

/**
 * A map to look up a route onestop ID given agencyID and routeID
 */
class RouteIDMap(private val store: MutableMap<String, MutableMap<FeedLocalRouteID, OneStopRouteID>> = mutableMapOf()) {
    fun add(agencyID: String, routeID: FeedLocalRouteID, onestopID: OneStopRouteID) {
        store.putIfAbsent(agencyID, mutableMapOf())
        store[agencyID]!![routeID] = onestopID
    }

    fun get(agencyID: String, routeID: FeedLocalRouteID): OneStopRouteID? = store[agencyID]?.get(routeID)
}
