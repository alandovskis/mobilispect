package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopIDDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * A [StopDataSource] that uses a GTFS feed as a source.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class GTFSStopDataSource(private val stopIDDataSource: StopIDDataSource) : StopDataSource {
    private val logger = LoggerFactory.getLogger(GTFSStopDataSource::class.java)
    override fun stops(root: String, version: String, feedID: String): Result<Collection<Stop>> =
        stopIDDataSource.stops(feedID)
            .map { stopIDMap ->
                return try {
                    val input = File(root, "stops.txt").readTextAndNormalize()
                    logger.trace("Input: $input")

                    val csv = Csv {
                        hasHeaderRecord = true
                        ignoreUnknownColumns = true
                    }
                    Result.success(csv.decodeFromString<Collection<GTFSStop>>(input).mapNotNull { stop ->
                        val id = stopIDMap.get(stop.stop_id) ?: return@mapNotNull null
                        Stop(
                            _id = id, name = stop.stop_name, version = version
                        )
                    })
                } catch (e: IOException) {
                    Result.failure(e)
                } catch (e: SerializationException) {
                    Result.failure(e)
                }
            }
}
