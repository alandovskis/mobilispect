package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.AgencyIDDataSource
import com.mobilispect.backend.schedule.route.Route
import com.mobilispect.backend.schedule.route.RouteDataSource
import com.mobilispect.backend.schedule.route.RouteIDDataSource
import com.mobilispect.backend.util.readTextAndNormalize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import java.io.IOException
import java.nio.file.Path

/**
 * A [RouteDataSource] that uses a GTFS feed as source.
 */
@OptIn(ExperimentalSerializationApi::class)
@Suppress("ReturnCount")
class GTFSRouteDataSource(
    private val agencyIDDataSource: AgencyIDDataSource,
    private val routeIDDataSource: RouteIDDataSource
) : RouteDataSource {
    override fun routes(root: Path, version: String, feedID: String): Result<Collection<Route>> {
        return try {
            val input = root.resolve("routes.txt").toFile().readTextAndNormalize()
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
                val feedLocalAgencyID = route.agency_id ?: agencyIDs.keys.firstOrNull() ?: return@mapNotNull null
                val onestopAgencyID = agencyIDs[feedLocalAgencyID] ?: return@mapNotNull null
                val routeID = routeIDs[route.route_id] ?: return@mapNotNull null
                Route(
                    id = routeID,
                    shortName = route.route_short_name,
                    longName = route.route_long_name,
                    agencyID = onestopAgencyID,
                    version = version
                )
            })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
