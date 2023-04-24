package com.mobilispect.backend.data.route

private val ONESTOP_ID_REGEX = Regex("r-[a-z0-9]+-[a-z0-9~çéâêîôûàèùëïü]+")

/**
 * A route ID that is unique globally.
 */
@JvmInline
value class OneStopRouteID(internal val str: String) {
    init {
        require(ONESTOP_ID_REGEX.matches(str)) { "Must be in OneStopID format i.e. r-geohash-name. Was $str" }
    }
}
