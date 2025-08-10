package com.mobilispect.backend

import com.mobilispect.backend.schedule.archive.ArchiveExtractor
import com.mobilispect.backend.schedule.download.DownloadRequest
import com.mobilispect.backend.schedule.download.Downloader
import com.mobilispect.backend.schedule.feed.VersionedFeed
import com.mobilispect.backend.schedule.route.RouteDataSource
import com.mobilispect.backend.schedule.schedule.ScheduledStopRepository
import com.mobilispect.backend.schedule.stop.StopDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.time.Clock
import java.time.Instant
import java.util.function.Supplier
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

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
    private val scheduledStopDataSource: ScheduledStopDataSource,
    private val clock: Clock = Clock.systemDefaultZone()
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
        val regions = regionRepository.findAll()
        val feeds = regions.flatMap { region -> feedDataSource.feeds(region.name) }
            .filter { it.isSuccess }
            .mapNotNull { it.getOrNull() }
        logger.debug("Found feeds: {}", feeds)
        return feeds
    }

    @Suppress("ReturnCount")
    private fun importFeed(cloudFeed: VersionedFeed): Result<*> {
        logger.debug("Import started: {}", cloudFeed)
        return downloadFeed(cloudFeed)
            .map { archive -> extractFeed(archive) }
            .map { extractedDirRes ->
                extractedDirRes.getOrNull()?.let { extractedDir ->
                    val agencyRes = importAgencies(
                        version = cloudFeed.version.uid,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed.uid
                    )
                    if (agencyRes.isFailure) {
                        return agencyRes
                    }

                    val routeRes = importRoutes(
                        version = cloudFeed.version.uid,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed.uid
                    )
                    if (routeRes.isFailure) {
                        return routeRes
                    }

                    val stopsRes = importStops(
                        version = cloudFeed.version.uid,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed.uid
                    )
                    if (stopsRes.isFailure) {
                        return stopsRes
                    }

                    val tripRes = importTrips(
                        version = cloudFeed.version.uid,
                        extractedDir = extractedDir,
                        feedID = cloudFeed.feed.uid
                    )
                    if (tripRes.isFailure) {
                        return tripRes
                    }

                    val stopTimeRes = importStopTimes(cloudFeed.version.uid, extractedDir)
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

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<Path> {
        val start = Instant.now(clock)
        return downloader.download(DownloadRequest(url = cloudFeed.feed.url))
            .onSuccess { archive ->
                val elapsed = java.time.Duration.between(start, Instant.now(clock))
                logger.debug("Downloaded feed from {} to {} in {}", cloudFeed.feed.url, archive, elapsed)
            }
            .onFailure { exception -> logger.error("Error downloading feed from ${cloudFeed.feed.url}: $exception") }
    }

    private fun extractFeed(archive: Path): Result<Path> {
        val start = clock.instant()
        return archiveExtractor.extract(archive)
            .onSuccess { path ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Extracted archive to {} in {}", path, elapsed)
            }
            .onFailure { exception -> logger.error("Error extracting feed: $exception") }
    }

    private fun importAgencies(version: String, extractedDir: Path, feedID: String): Result<Collection<Agency>> {
        val start = clock.instant()
        return agencyDataSource.agencies(root = extractedDir, version = version, feedID = feedID)
            .map { agencies -> agencies.map { agency -> agencyRepository.save(agency) } }
            .onSuccess { agencies ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Imported {} agencies in {}", agencies.size, elapsed)
            }
            .onFailure { e -> logger.error("Failed to import agencies: $e") }
    }

    private fun importRoutes(version: String, extractedDir: Path, feedID: String): Result<Collection<Route>> {
        val start = clock.instant()
        return routeDataSource.routes(root = extractedDir, version = version, feedID = feedID)
            .map { routes -> routes.map { route -> routeRepository.save(route) } }
            .onSuccess { routes ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Imported {} routes in {}", routes.size, elapsed)
            }
            .onFailure { e -> logger.error("Failed to import routes: $e") }
    }

    private fun importStops(version: String, extractedDir: Path, feedID: String): Result<Collection<Stop>> {
        val start = clock.instant()
        return stopDataSource.stops(extractedDir, version, feedID)
            .map { stops ->
                stops.map { stop -> return@map stopRepository.save(stop) }
            }
            .onSuccess { stops ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Imported {} stops in {}", stops.size, elapsed)
            }
            .onFailure { e -> logger.error("Failed to import stops: $e") }
    }

    private fun importTrips(version: String, extractedDir: Path, feedID: String): Result<Collection<ScheduledTrip>> {
        val start = clock.instant()
        return scheduledTripDataSource.trips(extractedDir, version, feedID)
            .map { trips -> trips.map { trip -> scheduledTripRepository.save(trip) } }
            .onSuccess { trips ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Imported {} trips in {}", trips.size, elapsed) }
            .onFailure { e -> logger.error("Failed to import scheduled trips: $e") }
    }

    private fun importStopTimes(version: String, extractedDir: Path): Result<Collection<ScheduledStop>> {
        val start = clock.instant()
        return scheduledStopDataSource.scheduledStops(extractedDir, version)
            .map { scheduledStops -> scheduledStops.map { stop -> scheduledStopRepository.save(stop) } }
            .onSuccess { stops ->
                val elapsed = java.time.Duration.between(start, clock.instant())
                logger.debug("Imported {} stop times in {}", stops.size, elapsed) }
            .onFailure { e -> logger.error("Failed to import stop times: $e") }
    }
}