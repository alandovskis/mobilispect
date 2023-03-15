package com.mobilispect.backend.data.agency

import com.mobilispect.backend.data.Agency

/**
 * A [RegionalAgencyDataSource] that is suitable for testing with.
 */
class FakeRegionalAgencyDataSource : RegionalAgencyDataSource {
    override fun agencies(apiKey: String, city: String, limit: Int, after: Int?): Result<AgencyResult> =
        Result.success(
            AgencyResult(
                agencies = listOf(
                    Agency(_id = "o-f25d-socitdetransportdemontral", name = "Société de transport de Montréal"),
                    Agency(_id = "o-f25-exo~reseaudetransportmetropolitain", name = "exo")
                ),
                after = 1
            )
        )
}
