package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.VersionedFeed
import org.slf4j.LoggerFactory

/**
 * A [FeedDataSource] that uses transit.land as its source.
 */
class TransitLandFeedDataSource(
    private val transitLandClient: TransitLandClient,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : FeedDataSource {
    private val logger = LoggerFactory.getLogger(TransitLandFeedDataSource::class.java)
    override fun feeds(region: String): Collection<Result<VersionedFeed>> {
        val apiKey =
            transitLandCredentialsRepository.get()
                ?: return listOf(Result.failure(IllegalStateException("Missing API key")))
        return transitLandClient.agencies(apiKey = apiKey, region = region)
            .map { result -> result.agencies.map { feed -> feed.feedID } }
            .onSuccess { feedIDs -> logger.debug("Found feed IDs: {}", feedIDs) }
            .onFailure { e -> logger.error("Unable to get feed IDs: $e") }
            .map { feedIDs ->
                feedIDs.map { feedID ->
                    transitLandClient.feed(
                        apiKey = apiKey,
                        feedID = feedID
                    )
                }
            }.getOrNull() ?: emptyList()
    }
}
