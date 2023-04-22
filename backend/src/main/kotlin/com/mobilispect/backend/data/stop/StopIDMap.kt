package com.mobilispect.backend.data.stop

class StopIDMap(private val store: MutableMap<String, String> = mutableMapOf<String, String>()) {

    fun add(stopID: String, oneStopID: String) {
        store[stopID] = oneStopID
    }

    fun get(stopID: String): String? = store[stopID]
}
