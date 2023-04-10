package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.archive.ArchiveExtractor
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.FeedRepository
import com.mobilispect.backend.data.feed.FeedVersionRepository
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteRepository
import com.mobilispect.backend.data.schedule.ScheduledStopDataSource
import com.mobilispect.backend.data.schedule.ScheduledStopRepository
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import com.mobilispect.backend.data.schedule.ScheduledTripRepository
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Supplier

/**
 * Import all updated feeds listed in [feedDataSource].
 */
@Service
class ImportUpdatedFeedsService(
    private val feedDataSource: FeedDataSource,
    private val feedRepository: FeedRepository,
    private val feedVersionRepository: FeedVersionRepository,
    private val downloader: Downloader,
    private val archiveExtractor: ArchiveExtractor,
    private val agencyRepository: AgencyRepository,
    private val routeRepository: RouteRepository,
    private val stopRepository: StopRepository,
    private val scheduledTripRepository: ScheduledTripRepository,
    private val scheduledStopRepository: ScheduledStopRepository,
    private val agencyDataSource: AgencyDataSource,
    private val routeDataSource: RouteDataSource,
    private val stopDataSource: StopDataSource,
    private val scheduledTripDataSource: ScheduledTripDataSource,
    private val scheduledStopDataSource: ScheduledStopDataSource
) : Supplier<Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportUpdatedFeedsService::class.java)

    override fun get(): Any {
        logger.info("\nStarted")
        return findUpdatedFeeds()
            .onSuccess { updatedFeeds ->
                if (updatedFeeds.isEmpty()) {
                    logger.info("Nothing to do\n")
                }
            }
            .map { updatedFeeds -> updatedFeeds.map { cloudFeed -> importFeed(cloudFeed) } }
            .onSuccess { logger.info("Completed") }
            .onFailure { exception -> logger.error("Failed: $exception") }
    }

    private fun findUpdatedFeeds(): Result<Collection<VersionedFeed>> = feedDataSource.feeds()
        .onSuccess { cloudFeeds -> logger.debug("Retrieved feeds: {}", cloudFeeds) }
        .onFailure { exception -> logger.error("Error retrieving feeds: $exception") }
        .map { cloudFeeds ->
            cloudFeeds.filter { cloudFeed ->
                val localVersions =
                    feedVersionRepository.findAllById(listOf(cloudFeed.version._id)).map { version -> version._id }
                return@filter !localVersions.contains(cloudFeed.version._id)
            }
        }.onSuccess { updatedFeeds -> logger.debug("Found updated feeds: {}", updatedFeeds) }

    private fun importFeed(cloudFeed: VersionedFeed): Result<*> = downloadFeed(cloudFeed)
        .map { archive -> extractFeed(archive) }
        .map { extractedDirRes ->
            extractedDirRes.getOrNull()?.let { extractedDir ->
                importAgencies(cloudFeed.version._id, extractedDir)
                importRoutes(cloudFeed.version._id, extractedDir)
                importStops(cloudFeed.version._id, extractedDir)
                importTrips(cloudFeed.version._id, extractedDir)
                importStopTimes(cloudFeed.version._id, extractedDir)
                feedRepository.save(cloudFeed.feed)
                feedVersionRepository.save(cloudFeed.version)
                cloudFeed
            }
        }

    private fun importAgencies(version: String, extractedDir: String) =
        agencyDataSource.agencies(extractedDir, version)
            .map { agencies -> agencies.forEach { agency -> agencyRepository.save(agency) } }
            .onSuccess { agencies -> logger.debug("Imported agencies: {}", agencies) }

    private fun importRoutes(version: String, extractedDir: String) =
        routeDataSource.routes(extractedDir, version)
            .map { routes -> routes.forEach { route -> routeRepository.save(route) } }
            .onSuccess { routes -> logger.debug("Imported routes: {}", routes) }

    private fun importStops(version: String, extractedDir: String) = stopDataSource.stops(extractedDir, version)
        .map { stops -> stops.forEach { stop -> stopRepository.save(stop) } }
        .onSuccess { stops -> logger.debug("Imported stops: {}", stops) }

    private fun importTrips(version: String, extractedDir: String) =
        scheduledTripDataSource.trips(extractedDir, version)
            .map { trips -> trips.forEach { trip -> scheduledTripRepository.save(trip) } }
            .onSuccess { trips ->
                logger.debug("Imported scheduled trips: {}", trips)
            }

    private fun importStopTimes(version: String, extractedDir: String) =
        scheduledStopDataSource.scheduledStops(extractedDir, version)
            .map { scheduledStops -> scheduledStops.map { stop -> scheduledStopRepository.save(stop) } }
            .onSuccess { logger.debug("Imported scheduled stops") }

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<String> = downloader.download(cloudFeed.feed.url)
        .onSuccess { archive -> logger.debug("Downloaded feed from {} to {}", cloudFeed.feed.url, archive) }
        .onFailure { exception -> logger.error("Error downloading feed from ${cloudFeed.feed.url}: $exception") }

    private fun extractFeed(archive: String): Result<String> = archiveExtractor.extract(archive)
        .onSuccess { path -> logger.debug("Extracted archive to {}", path) }
}
