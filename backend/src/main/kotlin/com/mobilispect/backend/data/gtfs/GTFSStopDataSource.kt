package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException

/**
 * A [StopDataSource] that uses a GTFS feed as a source.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class GTFSStopDataSource : StopDataSource {
    override fun stops(root: String, version: String): Result<Collection<Stop>> {
        return try {
            val input = File(root, "stops.txt").readTextAndNormalize()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            Result.success(csv.decodeFromString<Collection<GTFSStop>>(input).map { stop ->
                Stop(
                    _id = stop.stop_id, name = stop.stop_name, version = version
                )
            })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
