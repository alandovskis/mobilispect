package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException

/**
 * An [AgencyDataSource] that uses a GTFS feed as a source for agencies.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class GTFSAgencyDataSource : AgencyDataSource {
    override fun agencies(root: String, version: String): Result<Collection<Agency>> {
        return try {
            val input = File(root, "agency.txt").readTextAndNormalize()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            Result.success(csv.decodeFromString<Collection<GTFSAgency>>(input)
                .map { agency -> Agency(_id = agency.agency_id, name = agency.agency_name, version = version) })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}