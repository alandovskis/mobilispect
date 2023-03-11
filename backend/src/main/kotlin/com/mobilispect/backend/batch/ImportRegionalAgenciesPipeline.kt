package com.mobilispect.backend.batch

import com.mobilispect.backend.data.Agency
import com.mobilispect.backend.data.AgencyRepository
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import java.util.function.Function

/**
 * Import all agencies serving a given region from [networkDataSource].
 */
class ImportRegionalAgenciesPipeline(
    private val agencyRepository: AgencyRepository,
    private val networkDataSource: RegionalAgencyDataSource
) : Function<String, Any> {
    override fun apply(city: String): Any {
        val remoteAgenciesRes = extractRemote(city)
        if (remoteAgenciesRes.isFailure) {
            return Any()
        }
        val remoteAgencies = remoteAgenciesRes.getOrNull() ?: emptyList()
        val localAgencies = readLocal()

        val result = transform(localAgencies, remoteAgencies)
        load(result)

        return Any()
    }

    private fun readLocal(): Collection<Agency> = agencyRepository.findAll()

    private fun extractRemote(city: String): Result<Collection<Agency>> =
        networkDataSource.agencies(apiKey = "a", city = city)
            .map { data -> data.agencies }

    private fun transform(localAgencies: Collection<Agency>, remoteAgencies: Collection<Agency>): Collection<Agency> {
        val localIDs = localAgencies.map { agency -> agency._id }
        return remoteAgencies.filter { agency: Agency -> !localIDs.contains(agency._id) }
    }

    private fun load(agencies: Collection<Agency>) {
        for (agency in agencies) {
            agencyRepository.save(agency)
        }
    }
}
