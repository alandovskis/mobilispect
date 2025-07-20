package com.mobilispect.backend

import com.mobilispect.backend.schedule.stop.StopDataSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.nio.file.Path

/**
 * A [StopDataSource] that uses a GTFS feed as a source.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class GTFSStopDataSource(private val stopIDDataSource: StopIDDataSource) : StopDataSource {
    override fun stops(root: Path, version: String, feedID: String): Result<List<Stop>> {
        val input = root.resolve("stops.txt").toFile().readText()

        val csv = Csv {
            hasHeaderRecord = true
            ignoreUnknownColumns = true
        }

        val gtfsStops = csv.decodeFromString<Collection<GTFSStop>>(input)
        val stops: MutableList<Stop> = mutableListOf()
        for (stop in gtfsStops) {
            val uidRes = stopIDDataSource.stop(feedID, stop.stop_id)
            val uid = uidRes.getOrNull() ?: continue
            stops.add(Stop(uid = uid, localID = stop.stop_id, name = stop.stop_name, versions = listOf(version)))
        }
        return Result.success(stops)
    }
}
