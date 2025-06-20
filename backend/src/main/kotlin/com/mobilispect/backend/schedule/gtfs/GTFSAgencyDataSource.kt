package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.Agency
import com.mobilispect.backend.schedule.agency.AgencyDataSource
import com.mobilispect.backend.schedule.agency.AgencyIDDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Path

/**
 * An [AgencyDataSource] that uses a GTFS feed as a source.
 */
@OptIn(ExperimentalSerializationApi::class)
@Component
internal class GTFSAgencyDataSource(
    private val agencyIDDataSource: AgencyIDDataSource
) : AgencyDataSource {
    override fun agencies(root: Path, version: String, feedID: String): Result<Collection<Agency>> {
        val agencyIDRes = agencyIDDataSource.agencyIDs(feedID)
        if (agencyIDRes.isFailure) {
            return Result.failure(Exception("Missing agency IDs"))
        }
        val agencyIDs = agencyIDRes.getOrNull()!!

        return try {
            val input = root.resolve("agency.txt").toFile().readTextAndNormalize()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            val agencies = csv.decodeFromString<Collection<GTFSAgency>>(input)
                .mapNotNull { agency ->
                    val id = agencyIDs[agency.agency_id] ?: return@mapNotNull null
                    Agency(
                        id = id,
                        name = agency.agency_name,
                        version = version
                    )
                }
            Result.success(agencies)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
