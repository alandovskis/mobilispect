package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.archive.ArchiveExtractor
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.FeedRepository
import com.mobilispect.backend.data.feed.FeedVersionRepository
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.gtfs.GTFSStopTime
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteRepository
import com.mobilispect.backend.data.schedule.DateTimeOffset
import com.mobilispect.backend.data.schedule.ScheduledStop
import com.mobilispect.backend.data.schedule.ScheduledStopRepository
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import com.mobilispect.backend.data.schedule.ScheduledTripRepository
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.util.function.Supplier

private const val HOURS_PER_DAY = 24

/**
 * Import all updated feeds listed in [feedDataSource].
 */
@OptIn(ExperimentalSerializationApi::class)
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
    private val scheduledTripDataSource: ScheduledTripDataSource
) : Supplier<Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportUpdatedFeedsService::class.java)
    private val csv: Csv = Csv {
        hasHeaderRecord = true
        ignoreUnknownColumns = true
    }

    override fun get(): Any {
        logger.info("\nStarted")
        return feedDataSource.feeds().map { cloudFeeds ->
            val updatedFeeds = findUpdatedFeeds(cloudFeeds)
            updatedFeeds.forEach { cloudFeed -> importFeed(cloudFeed) }
            if (updatedFeeds.isNotEmpty()) {
                logger.info("Completed\n")
            } else {
                logger.info("Nothing to do\n")
            }
        }
    }

    private fun findUpdatedFeeds(cloudFeeds: Collection<VersionedFeed>): Collection<VersionedFeed> {
        val updatedFeeds = cloudFeeds.filter { cloudFeed ->
            val localVersions =
                feedVersionRepository.findAllById(listOf(cloudFeed.version._id)).map { version -> version._id }

            return@filter !localVersions.contains(cloudFeed.version._id)
        }
        logger.debug("Found updated feeds: {}", updatedFeeds)
        return updatedFeeds
    }

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
            }
        }

    private fun importAgencies(version: String, extractedDir: String) = agencyDataSource.agencies(extractedDir, version)
        .map { agencies -> agencies.forEach { agency -> agencyRepository.save(agency) } }
        .onSuccess { agencies -> logger.debug("Imported agencies: {}", agencies) }

    private fun importRoutes(version: String, extractedDir: String) =
        routeDataSource.routes(extractedDir, version)
            .map { routes -> routes.forEach { route -> routeRepository.save(route) } }
            .onSuccess { routes -> logger.debug("Imported routes: {}", routes) }

    private fun importStops(version: String, extractedDir: String) = stopDataSource.stops(extractedDir, version)
        .map { stops -> stops.forEach { stop -> stopRepository.save(stop) } }
        .onSuccess { stops -> logger.debug("Imported stops: {}", stops) }

    private fun importTrips(version: String, extractedDir: String) = scheduledTripDataSource.trips(extractedDir, version)
        .map { trips -> trips.forEach { trip -> scheduledTripRepository.save(trip) } }
        .onSuccess { trips -> logger.debug("Imported scheduled trips: {}", trips)
    }

    private fun importStopTimes(version: String, extractedDir: String) {
        val stopTimesIn = File("$extractedDir/stop_times.txt").readTextAndNormalize()
        csv.decodeFromString<Collection<GTFSStopTime>>(stopTimesIn)
            .mapNotNull { stopTime ->
                val departsAt = parseGTFSTime(stopTime.departure_time) ?: return@mapNotNull null
                val arrivesAt = parseGTFSTime(stopTime.arrival_time) ?: return@mapNotNull null
                ScheduledStop(
                    tripID = stopTime.trip_id,
                    stopID = stopTime.stop_id,
                    departsAt = departsAt,
                    arrivesAt = arrivesAt,
                    stopSequence = stopTime.stop_sequence,
                    version = version
                )
            }.forEach { scheduledStop -> scheduledStopRepository.save(scheduledStop) }
    }

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<String> {
        val result = downloader.download(cloudFeed.feed.url)
        result.onSuccess { archive ->
            logger.debug("Downloaded feed from {} to {}", cloudFeed.feed.url, archive)
        }
        return result
    }

    private fun extractFeed(archive: String): Result<String> = archiveExtractor.extract(archive)
        .onSuccess { path -> logger.debug("Extracted archive to {}", path) }

    /**
     * Parse a time in GTFS format (i.e. 25:00:00)
     */
    private fun parseGTFSTime(time: String): DateTimeOffset? {
        val split = time.split(":")
        if (split.size >= 3) {
            val hour = split[0].toIntOrNull() ?: return null
            return DateTimeOffset(
                days = hour / HOURS_PER_DAY,
                hours = hour % HOURS_PER_DAY,
                minutes = split[1].toIntOrNull() ?: return null,
                seconds = split[2].toIntOrNull() ?: return null
            )
        } else {
            return null
        }
    }
}