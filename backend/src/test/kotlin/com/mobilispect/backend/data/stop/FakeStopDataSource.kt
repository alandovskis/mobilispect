package com.mobilispect.backend.data.stop

import com.mobilispect.backend.data.api.PagingParameters

class FakeStopDataSource : StopDataSource {
    private val stops = arrayOf(
        listOf(
            Stop(
                _id = "s-f25dt17bg5-stationangrignon",
                name = "Station Angrignon",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            ),
            Stop(
                _id = "s-f25dt65h1j-stationmonk",
                name = "Station Monk",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            )
        ),
        listOf(
            Stop(
                _id = "s-f25dte5qq2-stationjolicoeur",
                name = "Station Jolicoeur",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            ),
            Stop(
                _id = "s-f25dtgdkrw-stationverdun",
                name = "Station Verdun",
                version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
            )
        ),
        listOf()
    )
    private var iterator: Iterator<Collection<Stop>> = stops.iterator()

    override fun stops(apiKey: String, agencyID: String, paging: PagingParameters): Result<StopResult> {
        val ret = iterator.next()
        return Result.success(
            StopResult(
                stops = ret,
                after = 1
            )
        )
    }

    fun all() = stops.flatMap { it }
}
