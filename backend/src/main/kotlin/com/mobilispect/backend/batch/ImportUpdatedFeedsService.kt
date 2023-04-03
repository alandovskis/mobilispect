package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.download.Downloader
import com.mobilispect.backend.data.feed.FeedDataSource
import com.mobilispect.backend.data.feed.FeedRepository
import com.mobilispect.backend.data.feed.FeedVersionRepository
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.gtfs.GTFSCalendar
import com.mobilispect.backend.data.gtfs.GTFSCalendarDate
import com.mobilispect.backend.data.gtfs.GTFSRoute
import com.mobilispect.backend.data.gtfs.GTFSStop
import com.mobilispect.backend.data.gtfs.GTFSStopTime
import com.mobilispect.backend.data.gtfs.GTFSTrip
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteRepository
import com.mobilispect.backend.data.schedule.DateTimeOffset
import com.mobilispect.backend.data.schedule.ScheduledStop
import com.mobilispect.backend.data.schedule.ScheduledStopRepository
import com.mobilispect.backend.data.schedule.ScheduledTrip
import com.mobilispect.backend.data.schedule.ScheduledTripRepository
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopRepository
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.function.Supplier
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
    private val agencyRepository: AgencyRepository,
    private val routeRepository: RouteRepository,
    private val stopRepository: StopRepository,
    private val scheduledTripRepository: ScheduledTripRepository,
    private val scheduledStopRepository: ScheduledStopRepository,
    private val agencyDataSource: AgencyDataSource
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
            updatedFeeds.forEach(importFeed())
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

    private fun importFeed(): (VersionedFeed) -> Unit = { cloudFeed ->
        downloadFeed(cloudFeed).map { archive ->
            val extractedDir = extractFeed(archive)
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

    private fun importRoutes(version: String, extractedDir: String) {
        val path = "$extractedDir/routes.txt"
        val input = File(path).readTextAndNormalize()
        val routes = csv.decodeFromString<Collection<GTFSRoute>>(input).map { route ->
            Route(
                _id = route.route_id,
                shortName = route.route_short_name,
                longName = route.route_long_name,
                agencyID = route.agency_id,
                version = version,
                headwayHistory = emptyList()
            )
        }.map { route -> routeRepository.save(route) }
        logger.debug("Imported routes: {}", routes)
    }

    private fun importStops(version: String, extractedDir: String) {
        val path = "$extractedDir/stops.txt"
        val input = File(path).readTextAndNormalize()
        val stops = csv.decodeFromString<Collection<GTFSStop>>(input).map { stop ->
            Stop(
                _id = stop.stop_id, name = stop.stop_name, version = version
            )
        }.map { stop -> stopRepository.save(stop) }
        logger.debug("Imported stops: {}", stops)
    }

    private fun importTrips(version: String, extractedDir: String) {
        val calendarDatesIn = File("$extractedDir/calendar_dates.txt").readTextAndNormalize()
        val calendarDates = csv.decodeFromString<Collection<GTFSCalendarDate>>(calendarDatesIn)
            .groupBy { calendarDate -> calendarDate.service_id }
        logger.debug("Imported calendar dates: {}", calendarDates)

        val calendarIn = File("$extractedDir/calendar.txt").readTextAndNormalize()
        val calendars =
            csv.decodeFromString<Collection<GTFSCalendar>>(calendarIn).associateBy { calendar -> calendar.service_id }
        logger.debug("Imported calendars: {}", calendars)

        val tripsIn = File("$extractedDir/trips.txt").readTextAndNormalize()
        val trips = csv.decodeFromString<Collection<GTFSTrip>>(tripsIn).map { trip ->
            val added =
                calendarDates[trip.service_id]?.filter { it.exception_type == GTFSCalendarDate.ADDED }
                    ?.map { it.date } ?: emptyList()
            val removed =
                calendarDates[trip.service_id]?.filter { it.exception_type == GTFSCalendarDate.REMOVED }
                    ?.map { it.date } ?: emptyList()
            val dates = calendars[trip.service_id]?.let { calendar ->
                listOf(
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.MONDAY
                    ) { it.monday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.TUESDAY
                    ) { it.tuesday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.WEDNESDAY
                    ) { it.wednesday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.THURSDAY
                    ) { it.thursday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.FRIDAY
                    ) { it.friday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.SATURDAY
                    ) { it.saturday == 1 },
                    allDayOfWeekInRange(
                        calendar, calendar.start_date, calendar.end_date, DayOfWeek.SUNDAY
                    ) { it.sunday == 1 },
                ).flatten()
            }?.filter { !removed.contains(it) } ?: emptyList()
            ScheduledTrip(
                _id = trip.trip_id,
                routeID = trip.route_id,
                direction = trip.trip_headsign,
                version = version,
                dates = (dates + added).sorted()
            )
        }.map { trip -> scheduledTripRepository.save(trip) }
        logger.debug("Imported scheduled trips: {}", trips)
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

    private fun allDayOfWeekInRange(
        calendar: GTFSCalendar,
        startDate: LocalDate,
        endDate: LocalDate,
        dayOfWeek: DayOfWeek,
        predicate: (GTFSCalendar) -> Boolean
    ): MutableList<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        if (predicate(calendar)) {
            var date = findFirstInRange(startDate, dayOfWeek)
            dates += date

            date = date.plusWeeks(1)
            while (!date.isAfter(endDate)) {
                dates += date
                date = date.plusWeeks(1)
            }
        }
        return dates
    }

    private fun findFirstInRange(startDate: LocalDate, dayOfWeek: DayOfWeek): LocalDate {
        var date = startDate
        // TODO: Are we guaranteed to find a dayOfWeek.
        while (date.dayOfWeek != dayOfWeek) {
            date = date.plusDays(1)
        }
        return date
    }

    private fun downloadFeed(cloudFeed: VersionedFeed): Result<String> {
        val result = downloader.download(cloudFeed.feed.url)
        result.onSuccess { archive ->
            logger.debug("Downloaded feed from {} to {}", cloudFeed.feed.url, archive)
        }
        return result
    }

    private fun extractFeed(archive: String): String {
        val archiveSplit = archive.split('.')
        val destDir = File(archiveSplit[0])

        val archiveInputStream = ZipInputStream(FileInputStream(archive))
        var zipEntry = archiveInputStream.nextEntry
        while (zipEntry != null) {
            val newFile = newFile(destDir, zipEntry)
            // fix for Windows-created archives
            val parent = newFile.parentFile
            if (!parent.isDirectory && !parent.mkdirs()) {
                throw IOException("Failed to create directory $parent")
            }

            val out = FileOutputStream(newFile)
            var len: Int
            val buffer = ByteArray(1024)
            while (archiveInputStream.read(buffer).also { len = it } > 0) {
                out.write(buffer, 0, len)
            }
            out.close()
            zipEntry = archiveInputStream.nextEntry
        }
        logger.debug("Extracted archive to ${destDir.path}")
        return destDir.path
    }

    @Throws(IOException::class)
// Source: https://www.baeldung.com/java-compress-and-uncompress
    private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath: String = destinationDir.canonicalPath
        val destFilePath: String = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }
        return destFile
    }

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