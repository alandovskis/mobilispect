package com.mobilispect.backend.batch

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import com.mobilispect.backend.data.route.RouteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Function

/**
 * A service to import transit routes operated by an agency from [networkDataSource]
 */
@Service
class ImportRoutesService(
    private val routeRepository: RouteRepository,
    private val networkDataSource: RouteDataSource
) : Function<String, Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportRoutesService::class.java)
    private val apiKey: String = "API_KEY"

    override fun apply(agencyID: String): Any {
        logger.info("Started - $agencyID")

        val remoteRes = extractRemote(agencyID)
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

    private fun readLocal(): Collection<Route> = routeRepository.findAll()

    private fun extractRemote(agencyID: String): Result<Collection<Route>> {
        val allRoutes = mutableListOf<Route>()
        var after: Int? = null
        do {
            val routesRes = networkDataSource.routes(
                apiKey = apiKey,
                agencyID = agencyID,
                paging = PagingParameters(limit = 100, after = after)
            ).map {
                after = it.after
                return@map it.routes
            }
            if (routesRes.isFailure) {
                return routesRes
            }
            val routes = routesRes.getOrNull()!!
            allRoutes += routes
        } while (routes.isNotEmpty())
        return Result.success(allRoutes)
    }

    private fun transform(localAgencies: Collection<Route>, remoteAgencies: Collection<Route>): Collection<Route> {
        val localMap = localAgencies.associateBy { route -> route._id }
        return remoteAgencies.filter { route: Route -> entityExistsOrIsNewer(localMap, route) }
    }

    private fun load(routes: Collection<Route>) {
        for (route in routes) {
            routeRepository.save(route)
        }
    }
}