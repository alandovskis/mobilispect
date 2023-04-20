package com.mobilispect.backend.data.route

/**
 * A map to look up a route onestop ID given agencyID and routeID
 */
class RouteIDMap(private val store: MutableMap<String, MutableMap<String, String>> = mutableMapOf()) {
    fun add(agencyID: String, routeID: String, onestopID: String) {
        store.putIfAbsent(agencyID, mutableMapOf())
        store[agencyID]!![routeID] = onestopID
    }

    fun get(agencyID: String, routeID: String): String? = store[agencyID]?.get(routeID)
}
