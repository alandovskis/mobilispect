package com.mobilispect.backend

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.IOException
import java.nio.file.Path
import java.time.Instant
import java.time.ZonedDateTime

private const val HOURS_PER_DAY = 24
private const val DATE_COMPONENT_LENGTH = 3

/**
 * A [ScheduledStopDataSource] that uses a GTFS feed as its source.
 */
@OptIn(ExperimentalSerializationApi::class)
class GTFSScheduledStopDataSource : ScheduledStopDataSource {
    override fun scheduledStops(extractedDir: Path, version: String): Result<Collection<ScheduledStop>> {
        return try {
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            val stopTimesIn = extractedDir.resolve("stop_times.txt").toFile().readText()
            Result.success(csv.decodeFromString<Collection<GTFSStopTime>>(stopTimesIn)
                .mapNotNull { stopTime ->
                    val departsAt = parseGTFSTime(stopTime.departure_time) ?: return@mapNotNull null
                    val arrivesAt = parseGTFSTime(stopTime.arrival_time) ?: return@mapNotNull null
                    ScheduledStop(
                        tripID = stopTime.trip_id,
                        stopID = stopTime.stop_id,
                        departsAt = departsAt,
                        arrivesAt = arrivesAt,
                        stopSequence = stopTime.stop_sequence,
                        versions = listOf(version)
                    )
                })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    /**
     * Parse a time in GTFS format (i.e., 25:00:00)
     */
    @Suppress("ReturnCount")
    private fun parseGTFSTime(time: String): ZonedDateTime? {
        return ZonedDateTime.parse(time)
        /*val split = time.split(":")
        return if (split.size >= DATE_COMPONENT_LENGTH) {
            Instant.ofEpochMilli(1)
            /*val hour = split[0].toIntOrNull() ?: return null
            DateTimeOffset(
                days = hour / HOURS_PER_DAY,
                hours = hour % HOURS_PER_DAY,
                minutes = split[1].toIntOrNull() ?: return null,
                seconds = split[2].toIntOrNull() ?: return null
            )*/
        } else {
            null
        }*/
    }
}
