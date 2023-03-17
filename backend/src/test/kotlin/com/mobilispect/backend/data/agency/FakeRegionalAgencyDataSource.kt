package com.mobilispect.backend.data.agency

import com.mobilispect.backend.data.api.PagingParameters

/**
 * A [RegionalAgencyDataSource] that is suitable for testing with.
 */
class FakeRegionalAgencyDataSource : RegionalAgencyDataSource {
    override fun agencies(apiKey: String, city: String, paging: PagingParameters): Result<AgencyResult> =
        Result.success(
            AgencyResult(
                agencies = listOf(
                    Agency(
                        _id = "o-f25d-socitdetransportdemontral",
                        name = "Société de transport de Montréal",
                        version = "d503db302c07cdc0f4d6ff23bdfbb899e73b9f68"
                    ),
                    Agency(
                        _id = "o-f25-exo~reseaudetransportmetropolitain",
                        name = "exo",
                        version = "f1174597ee6017d690d56f631092691533fb743b"
                    )
                ),
                after = 1
            )
        )
}
