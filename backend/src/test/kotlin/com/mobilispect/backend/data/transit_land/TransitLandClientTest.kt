package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
internal class TransitLandClientTest {
    private lateinit var subject: TransitLandClient

    @Test
    fun agencies_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = AgenciesDispatcher(responseCode = 200, responseBody = "{}")
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.agencies(apiKey = "apikey", city = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun agencies_rateLimited() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = AgenciesDispatcher(responseCode = 429, responseBody = "{}")
        mockServer.start()
        val webClient = webClient(mockServer)

        subject = TransitLandClient(webClient)
        val response = subject.agencies(apiKey = "apikey", city = "city").exceptionOrNull()

        assertThat(response).isInstanceOf(TooManyRequests::class.java)
    }

    @Test
    fun agencies_unauthorized() {
        val mockServer = MockWebServer()

        mockServer.dispatcher = AgenciesDispatcher(responseCode = 401, responseBody = TRANSIT_LAND_UNAUTHORIZED_FIXTURE)
        mockServer.start()
        val webClient = webClient(mockServer)

        subject = TransitLandClient(webClient)
        val response = subject.agencies(apiKey = "apikey", city = "city").exceptionOrNull()!!

        assertThat(response).isInstanceOf(Unauthorized::class.java)
    }

    @Test
    fun agencies_minimal() {
        val mockServer = MockWebServer()

        mockServer.dispatcher = AgenciesDispatcher(responseCode = 200, responseBody = TRANSIT_LAND_AGENCIES_MINIMAL)
        mockServer.start()
        val webClient = webClient(mockServer)

        subject = TransitLandClient(webClient)
        val response = subject.agencies(apiKey = "apikey", city = "city").getOrNull()!!

        assertThat(response.after).isEqualTo(3973)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_1)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_2)
    }

    @Test
    fun agencies_success() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = AgenciesDispatcher(responseCode = 200, responseBody = TRANSIT_LAND_AGENCIES_SUCCESS)
        mockServer.start()
        val webClient = webClient(mockServer)

        subject = TransitLandClient(webClient)
        val response = subject.agencies(apiKey = "apikey", city = "city").getOrNull()!!

        assertThat(response.after).isEqualTo(3973)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_1)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_2)
    }

    private fun webClient(mockServer: MockWebServer): WebClient = WebClient.builder()
        .baseUrl(mockServer.url("/api/v2/rest/").toString())
        .build()

    class AgenciesDispatcher(private val responseCode: Int, private val responseBody: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: throw IllegalArgumentException()
            require(path.contains("/api/v2/rest/agencies.json"))
            return MockResponse().setResponseCode(responseCode).setBody(
                responseBody
            ).setHeader("Content-Type", "application/json")
        }
    }
}
