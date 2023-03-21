package com.mobilispect.backend.batch

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyRepository
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.transit_land.TransitLandCredentialsRepository
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
    private val networkDataSource: RegionalAgencyDataSource,
    private val transitLandCredentialsRepository: TransitLandCredentialsRepository
) : Function<String, Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportRegionalAgenciesService::class.java)

    override fun apply(city: String): Any {
        logger.info("Started")

        val apiKey = transitLandCredentialsRepository.get()
        if (apiKey == null) {
            logger.error("Missing transit land credentials")
            return Any()
        }

        val remoteAgenciesRes = extractRemote(apiKey, city)
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

    private fun extractRemote(apiKey: String, city: String): Result<Collection<Agency>> =
        networkDataSource.agencies(apiKey = apiKey, city = city, paging = PagingParameters(limit = 100, after = null))
            .map { data -> data.agencies }

    private fun transform(localAgencies: Collection<Agency>, remoteAgencies: Collection<Agency>): Collection<Agency> {
        val localMap = localAgencies.associateBy { agency -> agency._id }
        return remoteAgencies.filter { agency: Agency -> entityExistsOrIsNewer(localMap, agency) }
    }

    private fun load(agencies: Collection<Agency>) {
        for (agency in agencies) {
            agencyRepository.save(agency)
        }
    }
}
