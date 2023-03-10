package com.mobilispect.backend.batch

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * A client to access the transitland API.
 */
class TransitLandClient(private val webClient: WebClient) {
    /**
     * Retrieve all [TransitLandAgency]s that serve a given [city].
     */
    @Suppress("ReturnCount")
    fun agencies(apiKey: String, city: String, limit: Int = 20, after: Int? = null): Result<TransitAgencyResult> {
        try {
            var responseBuilder = webClient.get()
                .uri("/agencies.json")
                .attribute("city", city)
                .attribute("limit", limit)
                .header("apikey", apiKey)
            if (after != null) {
                responseBuilder = responseBuilder.attribute("after", after)
            }

            val response = responseBuilder
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TransitLandAgencyResponse::class.java).block()
            return Result.success(TransitAgencyResult(response?.agencies.orEmpty(), response?.meta?.after ?: 0))
        } catch (e: WebClientRequestException) {
            return Result.failure(NetworkError(e))
        } catch (e: WebClientResponseException) {
            return when (e) {
                is WebClientResponseException.TooManyRequests -> Result.failure(TooManyRequests)
                else -> Result.failure(GenericError(e.toString()))
            }
        }
    }
}
