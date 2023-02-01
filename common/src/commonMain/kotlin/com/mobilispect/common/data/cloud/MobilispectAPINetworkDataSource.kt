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
import kotlinx.serialization.json.Json

/**
 * A [NetworkDataSource2] that access the Mobilispect API.
 */
class MobilispectAPINetworkDataSource(httpEngine: HttpClientEngine) : NetworkDataSource2 {
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
                host = "localhost"
                port = 8080
                url { protocol = URLProtocol.HTTP }
            }
        }
    }

    override suspend fun agencies(): Collection<NetworkAgency> {
        val response = client.get(Agencies())
        val body = response.body<Body>()
        return body._embedded.agencies
    }
}

@Resource("/agencies")
class Agencies(val sort: String? = null)

@kotlinx.serialization.Serializable
data class Body(val _embedded: Embedded)

@kotlinx.serialization.Serializable
data class Embedded(val agencies: Collection<NetworkAgency>)