package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyResult
import com.mobilispect.backend.data.agency.AgencyResultItem
import com.mobilispect.backend.data.api.GenericError
import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopResult
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDate

/**
 * A client to access the transitland API.
 */
@Component
class TransitLandClient(private val webClient: WebClient) {
    fun feed(apiKey: String, feedID: String): Result<VersionedFeed> {
        return handleError {
            val uri = "/feeds.json?onestop_id=$feedID"
            val response = get(uri, apiKey, TransitLandFeedResponse::class.java)
            val feeds = response?.feeds?.mapNotNull { remote ->
                val latestVersion = remote.feed_versions.firstOrNull() ?: return@mapNotNull null
                VersionedFeed(
                    feed = Feed(
                        _id = feedID,
                        url = latestVersion.url
                    ),
                    version = FeedVersion(
                        _id = latestVersion.sha1,
                        feedID = feedID,
                        startsOn = LocalDate.parse(latestVersion.earliest_calendar_date),
                        endsOn = LocalDate.parse(latestVersion.latest_calendar_date)
                    ),
                )
            } ?: emptyList()
            return@handleError Result.success(feeds.first())
        }
    }

    /**
     * Retrieve all [Agency] that serve a given [region].
     */
    @Suppress("ReturnCount")
    fun agencies(apiKey: String, region: String, paging: PagingParameters = PagingParameters()): Result<AgencyResult> {
        return handleError {
            val uri = pagedURI("/agencies.json?city_name=$region", paging)
            val response = get(uri, apiKey, TransitLandAgencyResponse::class.java)
            val agencies = response?.agencies?.map { remote ->
                AgencyResultItem(
                    id = remote.onestopID,
                    name = remote.name,
                    version = remote.feed.version,
                    feedID = remote.feed.feed.oneStopID
                )
            }
            return@handleError Result.success(AgencyResult(agencies.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    fun routes(apiKey: String, agencyID: String, paging: PagingParameters = PagingParameters()): Result<RouteResult> {
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

    fun stops(apiKey: String, agencyID: String, paging: PagingParameters = PagingParameters()): Result<StopResult> {
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
