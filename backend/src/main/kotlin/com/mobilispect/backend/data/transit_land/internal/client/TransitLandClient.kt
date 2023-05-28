package com.mobilispect.backend.data.transit_land.internal.client

import com.mobilispect.backend.data.agency.OneStopAgencyID
import com.mobilispect.backend.data.api.*
import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.transit_land.api.*
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.netty.http.client.HttpClient
import java.time.LocalDate

private const val CONNECT_TIMEOUT_ms = 5_000

/**
 * A client to access the transitland API.
 */
@Component
class TransitLandClient(builder: WebClient.Builder, baseURL: String = "https://transit.land/api/v2/rest") :
    TransitLandAPI {
    private val logger = LoggerFactory.getLogger(TransitLandClient::class.java)

    private val webClient: WebClient

    init {
        val httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_ms)
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(5))
                connection.addHandlerLast(WriteTimeoutHandler(5))
            }.doOnRequest { request, _ ->
                logger.trace(
                    "${
                        request.method().name()
                    } ${request.uri()} ${request.fullPath()} ->"
                )
            }.doOnResponse { response, _ ->
                logger.trace(
                    "<- {}: {} {}", response.uri(), response.status().codeAsText(), response.status().reasonPhrase()
                )
            }

        webClient = builder.baseUrl(baseURL).clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build()
    }

    /**
     * Retrieve the feed identified by [feedID].
     */
    override fun feed(apiKey: String, feedID: String): Result<VersionedFeed> {
        return handleError {
            val uri = "/feeds.json?onestop_id=$feedID"
            val response = get(uri, apiKey, TransitLandFeedResponse::class.java)
            val feeds = response?.feeds?.mapNotNull { remote ->
                val latestVersion = remote.feed_versions.firstOrNull() ?: return@mapNotNull null
                VersionedFeed(
                    feed = Feed(
                        _id = feedID, url = latestVersion.url
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
     * Retrieve all agencies that serve a given [region] or are contained in the feed identified by [feedID].
     */
    @Suppress("ReturnCount")
    override fun agencies(apiKey: String, region: String?, feedID: String?): Result<AgencyResult> {
        return handleError {
            var uri = "/agencies.json"
            if (region != null) {
                uri += "?city_name=$region"
            }
            if (feedID != null) {
                uri += "?feed_onestop_id=$feedID"
            }

            val response = get(uri, apiKey, TransitLandAgencyResponse::class.java)
            val agencies = response?.agencies?.mapNotNull { remote ->
                try {
                    val id = OneStopAgencyID(remote.onestop_id)
                    AgencyResultItem(
                        id = id,
                        name = remote.name,
                        version = remote.feed.version,
                        feedID = remote.feed.feed.oneStopID,
                        agencyID = remote.agencyID
                    )
                } catch (e: IllegalArgumentException) {
                    if (e.message?.startsWith("Must be in OneStopID format") == true) {
                        return@mapNotNull null
                    }
                    throw e
                }
            }
            return@handleError Result.success(AgencyResult(agencies.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    /**
     * Retrieve the routes contained in the feed identified by [feedID].
     */
    override fun routes(apiKey: String, feedID: String, paging: PagingParameters): Result<RouteResult> {
        return handleError {
            val uri = pagedURI("/routes.json?feed_onestop_id=$feedID", paging)
            val response = get(uri, apiKey, TransitLandRouteResponse::class.java)
            val routes = response?.routes?.map { remote ->
                RouteResultItem(
                    id = remote.onestopID, agencyID = remote.agency.agencyID, routeID = remote.routeID
                )
            }
            return@handleError Result.success(RouteResult(routes.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    /**
     * Retrieve all stops contained in the feed identified by [feedID].
     */
    override fun stops(apiKey: String, feedID: String, paging: PagingParameters): Result<StopResult> {
        return handleError {
            val uri = pagedURI("/stops.json?feed_onestop_ids=$feedID", paging)
            val response = get(uri, apiKey, TransitLandStopResponse::class.java)
            val stops = response?.stops?.map { remote ->
                StopResultItem(
                    id = remote.onestopID, stopID = remote.stopID
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
        uri: String, apiKey: String, clazz: Class<T>
    ): T? {
        return webClient.get().uri(uri).header("apikey", apiKey).accept(MediaType.APPLICATION_JSON).retrieve()
            .bodyToMono(clazz).block()
    }
}
