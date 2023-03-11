package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.batch.GenericError
import com.mobilispect.backend.batch.NetworkError
import com.mobilispect.backend.batch.TooManyRequests
import com.mobilispect.backend.data.Agency
import com.mobilispect.backend.data.agency.AgencyResult
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * A client to access the transitland API.
 */
internal class TransitLandClient(private val webClient: WebClient) : RegionalAgencyDataSource {
    /**
     * Retrieve all [Agency] that serve a given [city].
     */
    @Suppress("ReturnCount")
    override fun agencies(apiKey: String, city: String, limit: Int, after: Int?): Result<AgencyResult> {
        try {
            var uri = "/agencies.json?city_name=$city&limit=$limit"
            if (after != null) {
                uri += "&after=$after"
            }

            val response = webClient.get()
                .uri(uri)
                .header("apikey", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TransitLandAgencyResponse::class.java).block()
            val agencies = response?.agencies?.map { remote -> Agency(_id = remote.onestopID, name = remote.name) }
            return Result.success(AgencyResult(agencies.orEmpty(), response?.meta?.after ?: 0))
        } catch (e: WebClientRequestException) {
            return Result.failure(NetworkError(e))
        } catch (e: WebClientResponseException) {
            return when (e) {
                is WebClientResponseException.TooManyRequests -> Result.failure(TooManyRequests)
                else -> Result.failure(GenericError(e.cause.toString()))
            }
        }
    }
}
