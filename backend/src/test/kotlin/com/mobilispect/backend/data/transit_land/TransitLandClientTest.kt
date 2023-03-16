package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.batch.NetworkError
import com.mobilispect.backend.batch.TooManyRequests
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

private const val AGENCIES_URL = "/api/v2/rest/agencies.json"

@SpringBootTest
internal class TransitLandClientTest {
    private lateinit var subject: TransitLandClient

    @Test
    fun agencies_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                when (request.path) {
                    AGENCIES_URL -> MockResponse().setResponseCode(200).setBody("{}")
                        .setHeader("Content-Type", "application/json")

                    else -> throw IllegalArgumentException()
                }
        }
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
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                when (request.path) {
                    AGENCIES_URL -> MockResponse().setResponseCode(429).setBody("{}")
                        .setHeader("Content-Type", "application/json")

                    else -> throw IllegalArgumentException()
                }
        }
        mockServer.start()
        val webClient = webClient(mockServer)

        subject = TransitLandClient(webClient)
        val response = subject.agencies(apiKey = "apikey", city = "city").exceptionOrNull()

        assertThat(response).isInstanceOf(TooManyRequests::class.java)
    }

    @Test
    fun agencies_success() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                when (request.path) {
                    AGENCIES_URL -> MockResponse().setResponseCode(200).setBody(
                        TRANSIT_LAND_AGENCIES_SUCCESS
                    ).setHeader("Content-Type", "application/json")

                    else -> throw IllegalArgumentException()
                }
        }
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
}
