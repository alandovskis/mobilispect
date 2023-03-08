package com.mobilispect.backend.batch

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@OptIn(ExperimentalCoroutinesApi::class)
class TransitLandClientTest {
    private lateinit var subject: TransitLandClient

    @Test
    fun agencies_networkError() = runTest {
        val mockEngine = MockEngine { request ->
            assertThat(request.url.encodedPath).contains("agencies.json")
            throw IOException()
        }

        subject = TransitLandClient(mockEngine)
        val result = subject.agencies(city = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun agencies_rateLimited() = runTest {
        val mockEngine = MockEngine { request ->
            assertThat(request.url.encodedPath).contains("agencies.json")
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.TooManyRequests,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        subject = TransitLandClient(mockEngine)
        val response = subject.agencies(city = "city").exceptionOrNull()

        assertThat(response).isInstanceOf(TooManyRequests::class.java)
    }

    @Test
    fun agencies_success() = runTest {
        val mockEngine = MockEngine { request ->
            assertThat(request.url.encodedPath).contains("agencies.json")
            respond(
                content = ByteReadChannel(TRANSIT_LAND_AGENCIES_SUCCESS),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        subject = TransitLandClient(mockEngine)
        val response = subject.agencies("city").getOrNull()!!

        assertThat(response.after).isEqualTo(3973)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_1)
        assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_2)
    }
}