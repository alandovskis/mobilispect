package com.mobilispect.common.data.agency

/**
 * A reference to an agency (in OneStopID format).
 *
 * Ex: o-f25d-socitdetransportdemontral
 */
data class AgencyRef(private val geohash: String, private val agencyName: String) {
    val id: String = "o-$geohash-$agencyName"

    companion object {
        fun fromString(string: String?): AgencyRef? =
            string?.let {
                val split = it.split("-")
                AgencyRef(geohash = split[1], agencyName = split[2])
            }
    }
}