package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.schedule.DateTimeOffset
import com.mobilispect.backend.data.schedule.ScheduledStop
import com.mobilispect.backend.data.schedule.ScheduledStopDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException

private const val HOURS_PER_DAY = 24
private const val DATE_COMPONENT_LENGTH = 3

/**
 * A [ScheduledStopDataSource] that uses a GTFS feed as its source.
 */
@OptIn(ExperimentalSerializationApi::class)
class GTFSScheduledStopDataSource : ScheduledStopDataSource {
    override fun scheduledStops(extractedDir: String, version: String): Result<Collection<ScheduledStop>> {
        return try {
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            val stopTimesIn = File("$extractedDir/stop_times.txt").readTextAndNormalize()
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
                        version = version
                    )
                })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    /**
     * Parse a time in GTFS format (i.e. 25:00:00)
     */
    private fun parseGTFSTime(time: String): DateTimeOffset? {
        val split = time.split(":")
        return if (split.size >= DATE_COMPONENT_LENGTH) {
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
