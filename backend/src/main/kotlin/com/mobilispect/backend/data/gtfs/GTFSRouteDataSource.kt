package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException

/**
 * A [RouteDataSource] that uses a GTFS feed as source.
 */
@OptIn(ExperimentalSerializationApi::class)
class GTFSRouteDataSource : RouteDataSource {
    override fun routes(root: String, version: String): Result<Collection<Route>> {
        return try {
            val input = File(root, "routes.txt").readTextAndNormalize()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }
            Result.success(csv.decodeFromString<Collection<GTFSRoute>>(input).map { route ->
                Route(
                    _id = route.route_id,
                    shortName = route.route_short_name,
                    longName = route.route_long_name,
                    agencyID = route.agency_id,
                    version = version,
                    headwayHistory = emptyList()
                )
            })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}