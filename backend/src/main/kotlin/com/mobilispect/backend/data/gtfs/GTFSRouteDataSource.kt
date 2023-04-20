package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.OneStopAgencyIDDataSource
import com.mobilispect.backend.data.route.OneStopRouteIDDataSource
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
@Suppress("ReturnCount")
class GTFSRouteDataSource(
    private val agencyIDDataSource: OneStopAgencyIDDataSource,
    private val routeIDDataSource: OneStopRouteIDDataSource
) : RouteDataSource {
    override fun routes(root: String, version: String, feedID: String): Result<Collection<Route>> {
        return try {
            val input = File(root, "routes.txt").readTextAndNormalize()
            val csv = Csv {
                hasHeaderRecord = true
                ignoreUnknownColumns = true
            }

            val agencyIDRes = agencyIDDataSource.agencyIDs(feedID)
            if (agencyIDRes.isFailure) {
                return Result.failure(agencyIDRes.exceptionOrNull()!!)
            }
            val agencyIDs = agencyIDRes.getOrNull()!!

            val routeIDRes = routeIDDataSource.routeIDs(feedID)
            if (routeIDRes.isFailure) {
                return Result.failure(routeIDRes.exceptionOrNull()!!)
            }
            val routeIDs = routeIDRes.getOrNull()!!

            Result.success(csv.decodeFromString<Collection<GTFSRoute>>(input).mapNotNull { route ->
                val agencyID = agencyIDs[route.agency_id] ?: return@mapNotNull null
                val routeID = routeIDs.get(route.agency_id, route.route_id) ?: return@mapNotNull null
                Route(
                    _id = routeID,
                    shortName = route.route_short_name,
                    longName = route.route_long_name,
                    agencyID = agencyID,
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
