package com.mobilispect.backend.schedule.gtfs

import arrow.core.Ior
import com.mobilispect.backend.infastructure.Stop
import com.mobilispect.backend.infastructure.StopIDDataSource
import com.mobilispect.backend.schedule.stop.StopDataSource
import com.mobilispect.backend.util.measureTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * A [StopDataSource] that uses a GTFS feed as a source.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class GTFSStopDataSource(
    private val stopIDDataSource: StopIDDataSource,
) : StopDataSource {
    private val logger = LoggerFactory.getLogger(GTFSStopDataSource::class.java)

    override fun stops(root: Path, version: String, feedID: String): Ior<Collection<Throwable>, Collection<Stop>> {
        return try {
            val input = root.resolve("stops.txt").toFile().readText()

            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }

            val (decodingTime, gtfsStops) = measureTime() {
                return@measureTime csv.decodeFromString<Collection<GTFSStop>>(input)
            }
            logger.debug("Decoded {} stops in {}", gtfsStops.size, decodingTime)

            val importedStops: MutableList<Stop> = mutableListOf()
            val errorStops: MutableList<Throwable> = mutableListOf()
            for (stop in gtfsStops) {
                val uidRes = stopIDDataSource.stop(feedID, stop.stop_id)
                if (uidRes.isSuccess) {
                    importedStops.add(
                        Stop(
                            uid = uidRes.getOrNull()!!,
                            localID = stop.stop_id,
                            name = stop.stop_name,
                            versions = listOf(version)
                        )
                    )
                } else {
                    errorStops.add(uidRes.exceptionOrNull()!!)
                }
            }

            return Ior.Both(errorStops, importedStops)
        } catch (IOException: Exception) {
            Ior.Left(listOf(IOException))
        } catch (SerializationException: Exception) {
            Ior.Left(listOf(SerializationException))
        }
    }
}
