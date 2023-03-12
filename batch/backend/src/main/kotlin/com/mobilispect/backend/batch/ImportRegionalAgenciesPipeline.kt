package com.mobilispect.backend.batch

import com.mobilispect.backend.data.Agency
import com.mobilispect.backend.data.AgencyRepository
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.function.Function

/**
 * Import all agencies serving a given region from [networkDataSource].
 */
@Service
class ImportRegionalAgenciesPipeline(
    private val agencyRepository: AgencyRepository,
    private val networkDataSource: RegionalAgencyDataSource
) : Function<String, Any> {
    private val logger: Logger = LoggerFactory.getLogger(ImportRegionalAgenciesPipeline::class.java)
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
        networkDataSource.agencies(apiKey = apiKey, city = city, limit = 100)
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
