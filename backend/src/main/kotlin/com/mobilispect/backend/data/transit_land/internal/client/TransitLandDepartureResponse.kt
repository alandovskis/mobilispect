@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("unused") // Deleting any of the unused fields will be break deserialization.

package com.mobilispect.backend.data.transit_land.internal.client

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
internal class TransitLandDepartureResponse(
    val stops: Collection<TransitLandDepartureItem>
)

@Serializable
internal class TransitLandDepartureItem(
    val departures: Collection<TransitLandDeparture>,
    @JsonNames("feed_version") val feed: FeedVersion? = null,
    val geometry: PointGeometry? = null,
    val id: Int? = null,
    @JsonNames("location_type") val locationType: Int? = null,
    @JsonNames("onestop_id") val onestopID: String,
    val parent: DepartureParentStop? = null,
    @JsonNames("platform_code") val platformCode: String? = null,
    @JsonNames("stop_code") val stopCode: String? = null,
    @JsonNames("stop_desc") val description: String? = null,
    @JsonNames("stop_id") val stopID: String? = null,
    @JsonNames("stop_name") val name: String? = null,
    @JsonNames("stop_timezone") val timezone: String? = null,
    @JsonNames("stop_url") val url: String? = null,
    @JsonNames("tts_stop_name") val ttsStopName: String? = null,
    @JsonNames("wheelchair_boarding") val wheelchairBoarding: Int? = null,
    @JsonNames("zone_id") val zoneID: String? = null
)

@Serializable
class TransitLandDeparture(
    val arrival: TransitLandDepartureTime,
    @JsonNames("arrival_time") val arrivalTime: String? = null,
    @JsonNames("continuous_drop_off") val continuousDropOff: Int? = null,
    @JsonNames("continuous_pickup") val continuousPickup: Int? = null,
    val departure: TransitLandDepartureTime,
    @JsonNames("departure_time") val departureTime: String? = null,
    @JsonNames("drop_off_type") val dropOffType: Int? = null,
    val interpolated: Boolean? = null,
    @JsonNames("pickup_type") val pickupType: Int? = null,
    @JsonNames("service_date") val serviceDate: String,
    @JsonNames("shape_dist_traveled") val distanceTravelled: Int? = null,
    @JsonNames("stop_headsign") val stopHeadsign: String? = null,
    @JsonNames("stop_sequence") val stopSequence: Int? = null,
    val timepoint: Boolean? = null,
    val trip: TransitLandDepartureTrip
)

@Suppress("PropertyName")
@Serializable
class TransitLandDepartureTime(
    @JsonNames("delay") val delay_s: Int? = null,
    val estimated: String? = null,
    @JsonNames("estimated_utc") val estimatedUTC: String? = null,
    val scheduled: String?,
    val uncertainty: Int? = null
)

@Serializable
class TransitLandDepartureTrip(
    @JsonNames("bikes_allowed") val bikesAllowed: Int? = null,
    @JsonNames("block_id") val blockID: String? = null,
    @JsonNames("direction_id") val directionID: Int? = null,
    val frequencies: Collection<TransitLandFrequency> = emptyList(),
    val id: Int? = null,
    val route: TransitLandDepartureRoute,
    val shape: TransitLandDepartureShape? = null,
    @JsonNames("stop_pattern_id") val stopPatternID: Int? = null,
    val timestamp: String? = null,
    @JsonNames("trip_headsign") val headsign: String,
    @JsonNames("trip_id") val tripID: String? = null,
    @JsonNames("trip_short_name") val shortName: String? = null,
    @JsonNames("wheelchair_accessible") val wheelchairAccessible: Int? = null
)

@Serializable
class TransitLandDepartureShape(
    val generated: Boolean,
    val id: Int,
    @JsonNames("shape_id") val shapeID: String
)

@Serializable
class TransitLandFrequency

@Serializable
class TransitLandDepartureRoute(
    val agency: TransitLandDepartureAgency? = null,
    @JsonNames("continuous_drop_off") val continuousDropOff: Int? = null,
    @JsonNames("continuous_pickup") val continuousPickup: Int? = null,
    val id: Int? = null,
    @JsonNames("onestop_id") val onestopID: String,
    @JsonNames("route_color") val colour: String? = null,
    @JsonNames("route_desc") val description: String? = null,
    @JsonNames("route_id") val routeID: String? = null,
    @JsonNames("route_long_name") val longName: String? = null,
    @JsonNames("route_short_name") val shortName: String? = null,
    @JsonNames("route_text_color") val textColour: String? = null,
    @JsonNames("route_type") val type: Int? = null,
    @JsonNames("route_url") val url: String? = null
)

@Serializable
class TransitLandDepartureAgency(
    @JsonNames("agency_id") val agencyID: String,
    @JsonNames("agency_name") val name: String,
    val id: Int,
    @JsonNames("onestop_id") val onestopID: String
)

@Serializable
internal class DepartureParentStop(
    val geometry: PointGeometry? = null,
    val id: Int,
    @JsonNames("location_type") val locationType: Int? = null,
    @JsonNames("onestop_id") val onestopID: String,
    @JsonNames("platform_code") val platformCode: String? = null,
    @JsonNames("stop_code") val stopCode: String? = null,
    @JsonNames("stop_desc") val description: String? = null,
    @JsonNames("stop_id") val stopID: String? = null,
    @JsonNames("stop_name") val name: String? = null,
    @JsonNames("stop_timezone") val timezone: String? = null,
    @JsonNames("stop_url") val url: String? = null,
    @JsonNames("tts_stop_name") val ttsStopName: String? = null,
    @JsonNames("wheelchair_boarding") val wheelchairBoarding: Int? = null,
    @JsonNames("zone_id") val zoneID: String? = null
)
