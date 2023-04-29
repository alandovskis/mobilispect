package com.mobilispect.backend.data.agency

private val ONESTOP_ID_REGEX = Regex("o-[a-z0-9]+-[a-z0-9~çéâêîôûàèùëïü]+")

/**
 * An agency ID that is unique globally.
 */
class OneStopAgencyID(internal val str: String) {
    init {
        require(ONESTOP_ID_REGEX.matches(str)) { "Must be in OneStopID format i.e. o-geohash-name. Was $str" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OneStopAgencyID

        return str == other.str
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }

    override fun toString(): String {
        return str
    }
}
