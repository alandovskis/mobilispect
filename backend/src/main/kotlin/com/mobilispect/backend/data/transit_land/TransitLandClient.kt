package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.GenericError
import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.VersionedFeed
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
     * Retrieve all feed IDs that serve a given [region].
     */
    fun feedsFor(apiKey: String, region: String): Result<Collection<String>> {
        return handleError {
            val uri = "/agencies.json?city_name=$region"
            val response = get(uri, apiKey, TransitLandAgencyResponse::class.java)
            val feedIDs = response?.agencies?.map { remote -> remote.feed.feed.oneStopID }
            return@handleError Result.success(feedIDs.orEmpty())
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
