package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyResult
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import com.mobilispect.backend.data.api.GenericError
import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.schedule.ScheduledStop
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopResult
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val HOURS_PER_DAY = 24

/**
 * A client to access the transitland API.
 */
class TransitLandClient(private val webClient: WebClient) : RegionalAgencyDataSource, RouteDataSource, StopDataSource {
    /**
     * Retrieve all [Agency] that serve a given [city].
     */
    @Suppress("ReturnCount")
    override fun agencies(apiKey: String, city: String, paging: PagingParameters): Result<AgencyResult> {
        return handleError {
            val uri = pagedURI("/agencies.json?city_name=$city", paging)
            val response = get(uri, apiKey, TransitLandAgencyResponse::class.java)
            val agencies = response?.agencies?.map { remote ->
                Agency(
                    _id = remote.onestopID,
                    name = remote.name,
                    version = remote.feed.version
                )
            }
            return@handleError Result.success(AgencyResult(agencies.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    override fun routes(apiKey: String, agencyID: String, paging: PagingParameters): Result<RouteResult> {
        return handleError {
            val uri = pagedURI("/routes.json?operator_onestop_id=$agencyID", paging)
            val response = get(uri, apiKey, TransitLandRouteResponse::class.java)
            val routes = response?.routes?.map { remote ->
                Route(
                    _id = remote.onestopID,
                    shortName = remote.shortName,
                    longName = remote.longName,
                    agencyID = agencyID,
                    version = remote.feed.version,
                    headwayHistory = emptyList()
                )
            }
            return@handleError Result.success(RouteResult(routes.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    override fun stops(apiKey: String, agencyID: String, paging: PagingParameters): Result<StopResult> {
        return handleError {
            val uri = pagedURI("/stops.json?served_by_onestop_ids=$agencyID", paging)
            val response = get(uri, apiKey, TransitLandStopResponse::class.java)
            val stops = response?.stops?.map { remote ->
                Stop(
                    _id = remote.onestopID,
                    name = remote.name,
                    version = remote.feed.version,
                )
            }
            return@handleError Result.success(StopResult(stops.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    fun departures(
        apiKey: String,
        stopID: String,
        serviceDate: LocalDate,
        paging: PagingParameters = PagingParameters()
    ): Result<Collection<ScheduledStop>> {
        return handleError {
            val uri = pagedURI(
                "/stops/$stopID/departures?service_data=${serviceDate.format(DateTimeFormatter.ISO_DATE)}",
                paging
            )
            val response = get(uri, apiKey, TransitLandDepartureResponse::class.java)
            val departures = response?.stops?.flatMap { remote -> remote.departures }?.map { departure ->
                ScheduledStop(
                    routeID = departure.trip.route.onestopID,
                    stopID = stopID,
                    departsAt = departure.departure.scheduled?.let { time -> parseGTFSTime(serviceDate, time) },
                    arrivesAt = departure.arrival.scheduled?.let { time -> parseGTFSTime(serviceDate, time) },
                    direction = departure.trip.headsign
                )
            }
            return@handleError Result.success(departures.orEmpty())
        }
    }

    /**
     * Parse a time in GTFS format (i.e. 25:00:00)
     */
    private fun parseGTFSTime(date: LocalDate, time: String): LocalDateTime? {
        val split = time.split(":")
        if (split.size >= 3) {
            val hour = split[0].toIntOrNull() ?: return null
            val days = hour / HOURS_PER_DAY
            val hours = hour % HOURS_PER_DAY
            val minute = split[1].toIntOrNull() ?: return null
            val second = split[2].toIntOrNull() ?: return null
            return LocalDateTime.of(date.plusDays(days.toLong()), LocalTime.of(hours, minute, second))
        } else {
            return LocalDateTime.of(date, LocalTime.parse(time))
        }
    }

    private fun <T> handleError(
        block: () -> Result<T>
    ): Result<T> {
        return try {
            block()
        } catch (e: WebClientRequestException) {
            Result.failure(NetworkError(e))
        } catch (e: WebClientResponseException) {
            when (e) {
                is WebClientResponseException.Unauthorized -> Result.failure(Unauthorized)
                is WebClientResponseException.TooManyRequests -> Result.failure(TooManyRequests)
                else -> Result.failure(GenericError(e.cause.toString()))
            }
        }
    }

    private fun pagedURI(endpoint: String, paging: PagingParameters): String {
        var uri = "$endpoint&limit=${paging.limit}"
        if (paging.after != null) {
            uri += "&after=${paging.after}"
        }
        return uri
    }

    private fun <T> get(
        uri: String,
        apiKey: String,
        clazz: Class<T>
    ): T? {
        return webClient.get()
            .uri(uri)
            .header("apikey", apiKey)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(clazz)
            .block()
    }
}
