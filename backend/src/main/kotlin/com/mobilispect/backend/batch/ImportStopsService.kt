package com.mobilispect.backend.batch

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.stop.Stop
import com.mobilispect.backend.data.stop.StopDataSource
import com.mobilispect.backend.data.stop.StopRepository
import com.mobilispect.backend.data.transit_land.TransitLandCredentialsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Function

/**
 * A service to import transit stops served by an agency from [networkDataSource]
 */
@Service
class ImportStopsService(
    private val stopRepository: StopRepository,
    private val networkDataSource: StopDataSource,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : Function<String, Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportStopsService::class.java)

    override fun apply(agencyID: String): Any {
        logger.info("Started - $agencyID")

        val apiKey = transitLandCredentialsRepository.get()
        if (apiKey == null) {
            logger.error("Missing transit land credentials")
            return Any()
        }

        val remoteRes = extractRemote(apiKey, agencyID)
        if (remoteRes.isFailure) {
            logger.error("Error retrieving routes: ${remoteRes.exceptionOrNull()}")
            return Any()
        }
        val remote = remoteRes.getOrNull() ?: emptyList()
        val local = readLocal()

        val result = transform(local, remote)
        load(result)

        logger.info("Completed - $agencyID")
        return Any()
    }

    private fun readLocal(): Collection<Stop> = stopRepository.findAll()

    private fun extractRemote(apiKey: String, agencyID: String): Result<Collection<Stop>> {
        val allStops = mutableListOf<Stop>()
        var after: Int? = null
        do {
            val stopsRes = networkDataSource.stops(
                apiKey = apiKey,
                agencyID = agencyID,
                paging = PagingParameters(limit = 100, after = after)
            ).map {
                after = it.after
                return@map it.stops
            }

            var stops: Collection<Stop> = listOf(Stop(_id = "dummy", name = "dummy", version = "dummy"))
            if (stopsRes.isFailure) {
                val error = stopsRes.exceptionOrNull()!!
                if (error is TooManyRequests) {
                    Thread.sleep(1_000)
                    continue
                } else {
                    return stopsRes
                }
            }
            stops = stopsRes.getOrNull()!!
            allStops += stops
        } while (stops.isNotEmpty())
        return Result.success(allStops)
    }

    private fun transform(localAgencies: Collection<Stop>, remoteAgencies: Collection<Stop>): Collection<Stop> {
        val localMap = localAgencies.associateBy { route -> route._id }
        return remoteAgencies.filter { route: Stop -> entityExistsOrIsNewer(localMap, route) }
    }

    private fun load(routes: Collection<Stop>) {
        for (route in routes) {
            stopRepository.save(route)
        }
    }
}
