package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.Agency
import com.mobilispect.backend.data.agency.AgencyResult
import com.mobilispect.backend.data.agency.RegionalAgencyDataSource
import com.mobilispect.backend.data.api.GenericError
import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.PagingParameters
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.data.route.Route
import com.mobilispect.backend.data.route.RouteDataSource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * A client to access the transitland API.
 */
class TransitLandClient(private val webClient: WebClient) : RegionalAgencyDataSource, RouteDataSource {
    /**
     * Retrieve all [Agency] that serve a given [city].
     */
    @Suppress("ReturnCount")
    override fun agencies(apiKey: String, city: String, paging: PagingParameters): Result<AgencyResult> {
        return handleError {
            var uri = "/agencies.json?city_name=$city&limit=${paging.limit}"
            if (paging.after != null) {
                uri += "&after=${paging.after}"
            }

            val response = webClient.get()
                .uri(uri)
                .header("apikey", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TransitLandAgencyResponse::class.java).block()
            val agencies = response?.agencies?.map { remote ->
                Agency(
                    _id = remote.onestopID,
                    name = remote.name,
                    version = remote.feed.version
                )
            }
            return@handleError Result.success(AgencyResult(agencies.orEmpty(), response?.meta?.after ?: 0))
        }
    }

    override fun routes(apiKey: String, agencyID: String, paging: PagingParameters): Result<RouteResult> {
        return handleError {
            var uri = "/routes.json?operator_onestop_id=$agencyID&limit=${paging.limit}"
            if (paging.after != null) {
                uri += "&after=${paging.after}"
            }

            val response = webClient.get()
                .uri(uri)
                .header("apikey", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()

            val convertedResponse = response.bodyToMono(TransitLandRouteResponse::class.java).block()
            val routes = convertedResponse?.routes?.map { remote ->
                Route(
                    _id = remote.onestopID,
                    shortName = remote.shortName,
                    longName = remote.longName,
                    agencyID = agencyID,
                    version = remote.feed.version,
                    headwayHistory = emptyList()
                )
            }
            return@handleError Result.success(RouteResult(routes.orEmpty(), convertedResponse?.meta?.after ?: 0))
        }
    }

    private fun <T> handleError(
        block: () -> Result<T>
    ): Result<T> {
        return try {
            block()
        } catch (e: WebClientRequestException) {
            Result.failure(NetworkError(e))
        } catch (e: WebClientResponseException) {
            when (e) {
                is WebClientResponseException.Unauthorized -> Result.failure(Unauthorized)
                is WebClientResponseException.TooManyRequests -> Result.failure(TooManyRequests)
                else -> Result.failure(GenericError(e.cause.toString()))
            }
        }
    }
}
