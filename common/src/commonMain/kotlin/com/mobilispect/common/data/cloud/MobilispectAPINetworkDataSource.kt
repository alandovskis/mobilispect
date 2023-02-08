package com.mobilispect.common.data.cloud

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json

/**
 * A [NetworkDataSource] that access the Mobilispect API.
 */
class MobilispectAPINetworkDataSource(httpEngine: HttpClientEngine) : NetworkDataSource {
    private val client: HttpClient

    init {
        client = HttpClient(httpEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Resources)
            defaultRequest {
                host = "192.168.100.27"
                port = 8443
                url { protocol = URLProtocol.HTTPS }
            }
        }
    }

    override suspend fun agencies(): Result<Collection<NetworkAgency>> {
        return try {
            val response = client.get(Agencies())
            val body = response.body<Body<EmbeddedAgency>>()
            Result.success(body._embedded.agencies)
        } catch (e: IOException) {
            Result.failure(NetworkError)
        }
    }

    override suspend fun routesOperatedBy(agencyID: String): Result<Collection<NetworkRoute>> {
        return try {
            val response = client.get(Routes(agencyID))
            val body = response.body<Body<EmbeddedRoute>>()
            Result.success(body._embedded.routes)
        } catch (e: IOException) {
            Result.failure(NetworkError)
        }
    }

}

@Resource("/agencies")
class Agencies(val sort: String? = null)

@Resource("/routes/search/findAllByAgencyID")
class Routes(val id: String)

@kotlinx.serialization.Serializable
data class Body<T>(val _embedded: T)

@kotlinx.serialization.Serializable
data class EmbeddedAgency(val agencies: Collection<NetworkAgency>)

@kotlinx.serialization.Serializable
data class EmbeddedRoute(val routes: Collection<NetworkRoute>)
