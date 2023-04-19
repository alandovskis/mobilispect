package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.archive.ArchiveExtractor
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.FeedRepository
import com.mobilispect.backend.data.feed.FeedVersionRepository
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.region.RegionRepository
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
@Suppress("LongParameterList")
class ImportUpdatedFeedsService(
    private val feedDataSource: FeedDataSource,
    private val feedRepository: FeedRepository,
    private val feedVersionRepository: FeedVersionRepository,
    private val downloader: Downloader,
    private val archiveExtractor: ArchiveExtractor,
    private val regionRepository: RegionRepository,
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
        logger.info("Started")
        val updatedFeeds = findUpdatedFeeds()
        if (updatedFeeds.isEmpty()) {
            logger.info("Completed without updates")
            return Any()
        }

        val results = updatedFeeds.map { updatedFeed -> importFeed(updatedFeed) }

        if (results.all { result -> result.isSuccess }) {
            logger.info("Completed with updates")
        } else {
            logger.error("Completed with errors")
        }

        return Any()
    }

    private fun findUpdatedFeeds(): List<VersionedFeed> {
        val feeds = regionRepository.findAll()
            .flatMap { region -> feedDataSource.feeds(region.name) }
        logger.debug("Found feeds: {}", feeds)

        val updatedFeeds = feeds.mapNotNull { cloudFeedRes -> cloudFeedRes.getOrNull() }
            .filter { cloudFeed ->
                val localVersions = feedVersionRepository.findAllById(listOf(cloudFeed.version._id))
                    .map { version -> version._id }
                return@filter !localVersions.contains(cloudFeed.version._id)
            }
        logger.debug("Found updated feeds: {}", updatedFeeds)
        return updatedFeeds
    }

    private fun importFeed(cloudFeed: VersionedFeed): Result<*> {
        logger.debug("Import started: {}", cloudFeed)
        return downloadFeed(cloudFeed)
            .map { archive -> extractFeed(archive) }
            .map { extractedDirRes ->
                extractedDirRes.getOrNull()?.let { extractedDir ->
                    val agencyRes = importAgencies(
                        version = cloudFeed.version._id,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed._id
                    )
                    if (agencyRes.isFailure) {
                        return agencyRes
                    }

                    val routeRes = importRoutes(cloudFeed.version._id, extractedDir)
                    if (routeRes.isFailure) {
                        return routeRes
                    }

                    val stopRes = importStops(cloudFeed.version._id, extractedDir)
                    if (stopRes.isFailure) {
                        return stopRes
                    }

                    val tripRes = importTrips(cloudFeed.version._id, extractedDir)
                    if (tripRes.isFailure) {
                        return tripRes
                    }

                    val stopTimeRes = importStopTimes(cloudFeed.version._id, extractedDir)
                    if (stopTimeRes.isFailure) {
                        return stopTimeRes
                    }

                    feedRepository.save(cloudFeed.feed)
                    feedVersionRepository.save(cloudFeed.version)
                    cloudFeed
                }
            }
            .onSuccess { feed -> logger.debug("Import completed: {}", feed) }
    }

    private fun importAgencies(version: String, extractedDir: String, feedID: String): Result<List<Any>> =
        agencyDataSource.agencies(root = extractedDir, version = version, feedID = feedID)
            .map { agencies -> agencies.map { agency -> agencyRepository.save(agency) } }
            .onSuccess { agencies -> logger.debug("Imported agencies: {}", agencies) }
            .onFailure { e -> logger.error("Failed to import agencies: $e") }

    private fun importRoutes(version: String, extractedDir: String) =
        routeDataSource.routes(extractedDir, version)
            .map { routes -> routes.map { route -> routeRepository.save(route) } }
            .onSuccess { routes -> logger.debug("Imported routes: {}", routes) }
            .onFailure { e -> logger.error("Failed to import routes: $e") }

    private fun importStops(version: String, extractedDir: String) = stopDataSource.stops(extractedDir, version)
        .map { stops -> stops.map { stop -> stopRepository.save(stop) } }
        .onSuccess { stops -> logger.debug("Imported stops: {}", stops) }
        .onFailure { e -> logger.error("Failed to import stops: $e") }

    private fun importTrips(version: String, extractedDir: String) =
        scheduledTripDataSource.trips(extractedDir, version)
            .map { trips -> trips.map { trip -> scheduledTripRepository.save(trip) } }
            .onSuccess { trips -> logger.debug("Imported scheduled trips: {}", trips) }
            .onFailure { e -> logger.error("Failed to import scheduled trips: $e") }

    private fun importStopTimes(version: String, extractedDir: String) =
        scheduledStopDataSource.scheduledStops(extractedDir, version)
            .map { scheduledStops -> scheduledStops.map { stop -> scheduledStopRepository.save(stop) } }
            .onSuccess { logger.debug("Imported scheduled stops") }
            .onFailure { e -> logger.error("Failed to import scheduled stops: $e") }

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<String> = downloader.download(cloudFeed.feed.url)
        .onSuccess { archive -> logger.debug("Downloaded feed from {} to {}", cloudFeed.feed.url, archive) }
        .onFailure { exception -> logger.error("Error downloading feed from ${cloudFeed.feed.url}: $exception") }

    private fun extractFeed(archive: String): Result<String> = archiveExtractor.extract(archive)
        .onSuccess { path -> logger.debug("Extracted archive to {}", path) }
}
