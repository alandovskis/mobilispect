package com.mobilispect.backend.batch

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.*
import kotlinx.serialization.json.Json

/**
 * A client to access the transitland API.
 */
class TransitLandClient(httpEngine: HttpClientEngine) {
    private val client: HttpClient = HttpClient(httpEngine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Resources)
        defaultRequest {
            host = "transit.land"
            url { protocol = URLProtocol.HTTPS }
        }
    }

    /**
     * Retrieve all [TransitLandAgency]s that serve a given [city].
     */
    suspend fun agencies(city: String, limit: Int = 20, after: Int? = null): Result<TransitAgencyResult> {
        return try {
            val response = client.get(Agencies(city = city, limit = limit, after = after))
            return when (response.status) {
                HttpStatusCode.TooManyRequests -> return Result.failure(TooManyRequests)
                HttpStatusCode.OK, HttpStatusCode.Accepted, HttpStatusCode.Created,
                HttpStatusCode.NonAuthoritativeInformation, HttpStatusCode.NoContent, HttpStatusCode.ResetContent,
                HttpStatusCode.PartialContent, HttpStatusCode.MultiStatus -> {
                    val responseBody = response.body<TransitLandAgencyResponse>()
                    Result.success(TransitAgencyResult(responseBody.agencies, responseBody.meta.after))

                }

                else -> Result.failure(GenericError(response.status.description))
            }
        } catch (e: IOException) {
            Result.failure(NetworkError(e))
        }
    }

}

@Resource("/api/v2/rest/agencies.json")
class Agencies(val city: String, val limit: Int, val after: Int? = null)
