package com.mobilispect.backend.data.route

import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.transit_land.RouteResult

/**
 * A [RouteDataSource] suitable for testing.
 */
class FakeRouteDataSource : RouteDataSource {
    private val routes = arrayOf(
        listOf(
            Route(
                _id = "r-f25d-1",
                shortName = "1",
                longName = "Verte",
                agencyID = "o-f25d-socitdetransportdemontral",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68",
                headwayHistory = emptyList()
            ),
            Route(
                _id = "r-f25d-2",
                shortName = "2",
                longName = "Orange",
                agencyID = "o-f25d-socitdetransportdemontral",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68",
                headwayHistory = emptyList()
            )
        ), listOf(
            Route(
                _id = "r-f25d-4",
                shortName = "4",
                longName = "Jaune",
                agencyID = "o-f25d-socitdetransportdemontral",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68",
                headwayHistory = emptyList()
            ),
            Route(
                _id = "r-f25d-5",
                shortName = "5",
                longName = "Bleue",
                agencyID = "o-f25d-socitdetransportdemontral",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68",
                headwayHistory = emptyList()
            )
        ), emptyList()
    )
    private var iterator: Iterator<Collection<Route>> = routes.iterator()

    override fun routes(apiKey: String, agencyID: String, paging: PagingParameters): Result<RouteResult> {
        val ret = iterator.next()
        return Result.success(
            RouteResult(
                routes = ret,
                after = 1
            )
        )
    }

    fun all() = routes.flatMap { it }
}
