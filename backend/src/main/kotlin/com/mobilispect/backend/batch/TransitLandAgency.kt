@file:OptIn(ExperimentalSerializationApi::class)

package com.mobilispect.backend.batch

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
internal data class TransitLandAgencyResponse(val agencies: Collection<TransitLandAgency>, val meta: Meta)

@Serializable
data class Meta(val after: Int, val next: String? = null)

@Serializable
data class TransitLandAgency(
    @JsonNames("agency_email") val email: String? = null,
    @JsonNames("agency_fare_url") val fareURL: String? = null,
    @JsonNames("agency_id") val agencyID: String? = null,
    @JsonNames("agency_lang") val language: String? = null,
    @JsonNames("agency_name") val name: String,
    @JsonNames("agency_phone") val phone: String? = null,
    @JsonNames("agency_timezone") val timezone: String? = null,
    @JsonNames("agency_url") val url: String? = null,
    @JsonNames("feed_version") val feed: FeedVersion? = null,
    val geometry: Geometry? = null,
    val id: Int? = null,
    @JsonNames("onestop_id") val onestopID: String,
    val operator: Operator? = null,
    val places: Array<Place> = emptyArray(),
) {
    @Suppress("CyclomaticComplexMethod")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransitLandAgency

        if (email != other.email) return false
        if (fareURL != other.fareURL) return false
        if (agencyID != other.agencyID) return false
        if (language != other.language) return false
        if (name != other.name) return false
        if (phone != other.phone) return false
        if (timezone != other.timezone) return false
        if (url != other.url) return false
        if (feed != other.feed) return false
        if (geometry != other.geometry) return false
        if (id != other.id) return false
        if (onestopID != other.onestopID) return false
        if (operator != other.operator) return false
        if (!places.contentEquals(other.places)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email?.hashCode() ?: 0
        result = 31 * result + (fareURL?.hashCode() ?: 0)
        result = 31 * result + (agencyID?.hashCode() ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (timezone?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (feed?.hashCode() ?: 0)
        result = 31 * result + (geometry?.hashCode() ?: 0)
        result = 31 * result + (id ?: 0)
        result = 31 * result + onestopID.hashCode()
        result = 31 * result + (operator?.hashCode() ?: 0)
        result = 31 * result + places.contentHashCode()
        return result
    }
}

@Serializable
data class Operator(
    val feeds: Array<OperatorFeed>,
    val name: String,
    @JsonNames("onestop_id") val oneStopID: String,
    @JsonNames("short_name") val shortName: String,
    val tags: Tags? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Operator

        if (!feeds.contentEquals(other.feeds)) return false
        if (name != other.name) return false
        if (oneStopID != other.oneStopID) return false
        if (shortName != other.shortName) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feeds.contentHashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + oneStopID.hashCode()
        result = 31 * result + shortName.hashCode()
        result = 31 * result + (tags?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class Tags(@JsonNames("twitter_general") val twitter: String? = null)

@Serializable
data class OperatorFeed(
    val id: Int,
    val name: String?,
    @JsonNames("onestop_id") val onestopID: String,
    val spec: String
)

@Serializable
data class Place(
    @JsonNames("adm0_name") val country: String,
    @JsonNames("adm1_name") val provinceState: String,
    @JsonNames("city_name") val city: String? = null
)

@Serializable
data class FeedVersion(
    val feed: Feed,
    @JsonNames("fetched_at") val fetchedAt: String? = null,
    @JsonNames("id") val id: Int? = null,
    @JsonNames("sha1") val version: String
)

@Serializable
data class Feed(val id: Int? = null, @JsonNames("onestop_id") val oneStopID: String? = null)

@Serializable
data class Geometry(val coordinates: Array<Array<Array<Double>>>, val type: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Geometry

        if (!coordinates.contentDeepEquals(other.coordinates)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.contentDeepHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
