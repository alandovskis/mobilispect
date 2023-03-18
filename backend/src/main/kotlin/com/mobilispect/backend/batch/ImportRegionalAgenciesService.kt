package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import com.mobilispect.backend.data.api.PagingParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Function

/**
 * Import all agencies serving a given region from [networkDataSource].
 */
@Service
class ImportRegionalAgenciesService(
    private val agencyRepository: AgencyRepository,
    private val networkDataSource: RegionalAgencyDataSource
) : Function<String, Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportRegionalAgenciesService::class.java)
    private val apiKey = "API_KEY"

    override fun apply(city: String): Any {
        logger.info("Started")

        val remoteAgenciesRes = extractRemote(city)
        if (remoteAgenciesRes.isFailure) {
            logger.error("Error retrieving agencies: ${remoteAgenciesRes.exceptionOrNull()}")
            return Any()
        }
        val remoteAgencies = remoteAgenciesRes.getOrNull() ?: emptyList()
        val localAgencies = readLocal()

        val result = transform(localAgencies, remoteAgencies)
        load(result)

        logger.info("Completed")
        return Any()
    }

    private fun readLocal(): Collection<Agency> = agencyRepository.findAll()

    private fun extractRemote(city: String): Result<Collection<Agency>> =
        networkDataSource.agencies(apiKey = apiKey, city = city, paging = PagingParameters(limit = 100, after = null))
            .map { data -> data.agencies }

    private fun transform(localAgencies: Collection<Agency>, remoteAgencies: Collection<Agency>): Collection<Agency> {
        val localMap = localAgencies.associateBy { agency -> agency._id }
        return remoteAgencies.filter { agency: Agency -> agencyExistsOrIsNewer(localMap, agency) }
    }

    private fun load(agencies: Collection<Agency>) {
        for (agency in agencies) {
            agencyRepository.save(agency)
        }
    }

    private fun agencyExistsOrIsNewer(
        localMap: Map<String, Agency>,
        agency: Agency
    ) = !localMap.contains(agency._id) || (localMap[agency._id]!!.version != agency.version)
}
