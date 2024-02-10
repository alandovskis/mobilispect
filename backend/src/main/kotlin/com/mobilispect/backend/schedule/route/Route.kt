@file:Suppress("PropertyName", "ConstructorParameterNaming") // For _id and medianHeadway_min

package com.mobilispect.backend.schedule.route

import com.mobilispect.backend.schedule.agency.OneStopAgencyID
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = "routes")
class Route(
    val _id: String,
    val shortName: String,
    val longName: String,
    val agencyID: String,
    val version: String,
    val headwayHistory: List<HeadwayEntry>
) {
    constructor(
        id: OneStopRouteID,
        shortName: String,
        longName: String,
        agencyID: OneStopAgencyID,
        version: String
    ) : this(
        _id = id.str,
        shortName = shortName,
        longName = longName,
        agencyID = agencyID.str,
        version = version,
        headwayHistory = emptyList()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (_id != other._id) return false
        if (shortName != other.shortName) return false
        if (longName != other.longName) return false
        if (agencyID != other.agencyID) return false
        if (version != other.version) return false
        return headwayHistory == other.headwayHistory
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + shortName.hashCode()
        result = 31 * result + longName.hashCode()
        result = 31 * result + agencyID.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + headwayHistory.hashCode()
        return result
    }

    override fun toString(): String {
        return "Route(_id='$_id', shortName='$shortName', longName='$longName', agencyID='$agencyID', version='$version', headwayHistory=$headwayHistory)"
    }
}

data class HeadwayEntry(val medianHeadway_min: Double)
