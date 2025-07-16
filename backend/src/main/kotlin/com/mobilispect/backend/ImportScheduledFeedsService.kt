/*package com.mobilispect.backend.schedule

import com.mobilispect.backend.AgencyDataSource
import com.mobilispect.backend.AgencyRepository
import com.mobilispect.backend.FeedDataSource
import com.mobilispect.backend.schedule.archive.ArchiveExtractor
import com.mobilispect.backend.schedule.download.DownloadRequest
import com.mobilispect.backend.schedule.download.Downloader
import com.mobilispect.backend.FeedRepository
import com.mobilispect.backend.FeedVersionRepository
import com.mobilispect.backend.RouteRepository
import com.mobilispect.backend.ScheduledTripRepository
import com.mobilispect.backend.StopRepository
import com.mobilispect.backend.schedule.feed.VersionedFeed
import com.mobilispect.backend.RegionRepository
import com.mobilispect.backend.schedule.route.RouteDataSource
import com.mobilispect.backend.ScheduledStopDataSource
import com.mobilispect.backend.schedule.schedule.ScheduledStopRepository
import com.mobilispect.backend.ScheduledTripDataSource
import com.mobilispect.backend.schedule.stop.StopDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.function.Supplier

/**
 * Import all scheduled listed in [feedDataSource].
 */
@Service
@Suppress("LongParameterList")
class ImportScheduledFeedsService(
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
    private val logger: Logger = LoggerFactory.getLogger(ImportScheduledFeedsService::class.java)

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

        /*val updatedFeeds = feeds.mapNotNull { cloudFeedRes -> cloudFeedRes.getOrNull() }
            .filter { cloudFeed ->
                val localVersions = feedVersionRepository.findAllById(listOf(cloudFeed.version._id))
                    .map { version -> version._id }
                return@filter !localVersions.contains(cloudFeed.version._id)
            }*/
//        logger.debug("Found updated feeds: {}", updatedFeeds)
        return listOf()
    }

    @Suppress("ReturnCount")
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

                    val routeRes = importRoutes(
                        version = cloudFeed.version._id,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed._id
                    )
                    if (routeRes.isFailure) {
                        return routeRes
                    }

                    val stopRes = importStops(
                        version = cloudFeed.version._id,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed._id
                    )
                    if (stopRes.isFailure) {
                        return stopRes
                    }

                    val tripRes = importTrips(
                        version = cloudFeed.version._id,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed._id
                    )
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

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<Path> =
        downloader.download(DownloadRequest(url = cloudFeed.feed.url))
        .onSuccess { archive -> logger.debug("Downloaded feed from {} to {}", cloudFeed.feed.url, archive) }
        .onFailure { exception -> logger.error("Error downloading feed from ${cloudFeed.feed.url}: $exception") }

    private fun extractFeed(archive: Path): Result<Path> = archiveExtractor.extract(archive)
        .onSuccess { path -> logger.debug("Extracted archive to {}", path) }
        .onFailure { exception -> logger.error("Error extracting feed: $exception") }

    private fun importAgencies(version: String, extractedDir: Path, feedID: String): Result<List<Any>> =
        agencyDataSource.agencies(root = extractedDir, version = version, feedID = feedID)
            .map { agencies -> agencies.map { agency -> agencyRepository.save(agency) } }
            .onSuccess { agencies -> logger.debug("Imported agencies: {}", agencies) }
            .onFailure { e -> logger.error("Failed to import agencies: $e") }

    private fun importRoutes(version: String, extractedDir: Path, feedID: String): Result<List<Any>> =
        routeDataSource.routes(root = extractedDir, version = version, feedID = feedID)
            .map { routes -> routes.map { route -> routeRepository.save(route) } }
            .onSuccess { routes -> logger.debug("Imported routes: {}", routes) }
            .onFailure { e -> logger.error("Failed to import routes: $e") }

    private fun importStops(version: String, extractedDir: Path, feedID: String) =
        stopDataSource.stops(extractedDir, version, feedID)
            .map { stops -> stops.map { stop -> stopRepository.save(stop) } }
            .onSuccess { stops -> logger.debug("Imported stops: {}", stops) }
            .onFailure { e -> logger.error("Failed to import stops: $e") }

    private fun importTrips(version: String, extractedDir: Path, feedID: String) =
        scheduledTripDataSource.trips(extractedDir, version, feedID)
            .map { trips -> trips.map { trip -> scheduledTripRepository.save(trip) } }
            .onSuccess { trips -> logger.debug("Imported scheduled trips: {}", trips) }
            .onFailure { e -> logger.error("Failed to import scheduled trips: $e") }

    private fun importStopTimes(version: String, extractedDir: Path) =
        scheduledStopDataSource.scheduledStops(extractedDir, version)
            .map { scheduledStops -> scheduledStops.map { stop -> scheduledStopRepository.save(stop) } }
            .onSuccess { logger.debug("Imported scheduled stops") }
            .onFailure { e -> logger.error("Failed to import scheduled stops: $e") }
}
*/