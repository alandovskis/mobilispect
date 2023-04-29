package com.mobilispect.backend.data.route

private val ONESTOP_ID_REGEX = Regex("r-[a-z0-9]+-[a-z0-9~çéâêîôûàèùëïü]+")

/**
 * A route ID that is unique globally.
 */
class OneStopRouteID(internal val str: String) {
    init {
        require(ONESTOP_ID_REGEX.matches(str)) { "Must be in OneStopID format i.e. r-geohash-name. Was $str" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OneStopRouteID

        return str == other.str
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }

    override fun toString(): String {
        return str
    }
}
