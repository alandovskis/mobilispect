package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.schedule.ScheduledTrip
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
/**
 * A [ScheduledTripDataSource] that uses a GTFS feed as source for [ScheduledTrip]s.
 */
internal class GTFSScheduledTripDataSource : ScheduledTripDataSource {
    private val logger: Logger = LoggerFactory.getLogger(GTFSScheduledTripDataSource::class.java)

    override fun trips(extractedDir: String, version: String): Result<Collection<ScheduledTrip>> {
        return try {
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            val calendarExceptions = findCalendarExceptions(extractedDir, csv)
            val calendars = findCalendars(extractedDir, csv)

            val tripsIn = File("$extractedDir/trips.txt").readTextAndNormalize()
            Result.success(csv.decodeFromString<Collection<GTFSTrip>>(tripsIn).map { trip ->
                val added =
                    calendarExceptions[trip.service_id]?.filter { it.exception_type == GTFSCalendarDate.ADDED }
                        ?.map { it.date } ?: emptyList()
                val removed =
                    calendarExceptions[trip.service_id]?.filter { it.exception_type == GTFSCalendarDate.REMOVED }
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
            })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    private fun findCalendars(
        extractedDir: String,
        csv: Csv
    ): Map<String, GTFSCalendar> {
        val calendarIn = File("$extractedDir/calendar.txt").readTextAndNormalize()
        val calendars =
            csv.decodeFromString<Collection<GTFSCalendar>>(calendarIn)
                .associateBy { calendar -> calendar.service_id }
        logger.debug("Imported calendars: {}", calendars)
        return calendars
    }

    private fun findCalendarExceptions(
        extractedDir: String,
        csv: Csv
    ): Map<String, List<GTFSCalendarDate>> {
        val calendarDatesIn = File("$extractedDir/calendar_dates.txt").readTextAndNormalize()
        val calendarDates = csv.decodeFromString<Collection<GTFSCalendarDate>>(calendarDatesIn)
            .groupBy { calendarDate -> calendarDate.service_id }
        logger.debug("Imported calendar dates: {}", calendarDates)
        return calendarDates
    }

    private fun allDayOfWeekInRange(
        calendar: GTFSCalendar,
        startDate: LocalDate,
        endDate: LocalDate,
        dayOfWeek: DayOfWeek,
        predicate: (GTFSCalendar) -> Boolean
    ): List<LocalDate> {
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
}
