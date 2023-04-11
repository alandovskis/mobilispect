package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.util.readTextAndNormalize
import com.mobilispect.backend.util.withMockServer
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
internal class TransitLandClientTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    private lateinit var subject: TransitLandClient

    @Test
    fun feed_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = FeedDispatcher(responseCode = 200, resourcePath = null)
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun feed_rateLimited() {
        withMockServer(dispatcher = FeedDispatcher(responseCode = 429, resourcePath = null)) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun feed_unauthorized() {
        withMockServer(
            dispatcher = FeedDispatcher(
                responseCode = 401,
                resourcePath = "transit-land/common/unauthorized.json",
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun feed_minimal() {
        withMockServer(
            dispatcher = FeedDispatcher(
                responseCode = 200,
                resourcePath = "transit-land/feed/minimal.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil").getOrNull()!!

            assertThat(response).isEqualTo(TRANSIT_LAND_FEED_FIXTURE)
        }
    }

    @Test
    fun feed_success() {
        withMockServer(
            dispatcher = FeedDispatcher(
                responseCode = 200,
                resourcePath = "transit-land/feed/full.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil").getOrNull()!!

            assertThat(response).isEqualTo(TRANSIT_LAND_FEED_FIXTURE)
        }
    }

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
        withMockServer(dispatcher = AgenciesDispatcher(responseCode = 429, responseBody = "{}")) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", city = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun agencies_unauthorized() {
        withMockServer(
            dispatcher = AgenciesDispatcher(
                responseCode = 401,
                responseBody = TRANSIT_LAND_UNAUTHORIZED_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", city = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun agencies_minimal() {
        withMockServer(
            dispatcher = AgenciesDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_AGENCIES_MINIMAL
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", city = "city").getOrNull()!!

            assertThat(response.after).isEqualTo(3973)
            assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_1)
            assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_2)
        }
    }

    @Test
    fun agencies_success() {
        withMockServer(
            dispatcher = AgenciesDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_AGENCIES_SUCCESS
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", city = "city").getOrNull()!!

            assertThat(response.after).isEqualTo(3973)
            assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_1)
            assertThat(response.agencies).contains(TRANSIT_LAND_SUCCESS_AGENCY_2)
        }
    }

    @Test
    fun routes_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = RoutesDispatcher(responseCode = 200, responseBody = "{}")
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.routes(apiKey = "apikey", agencyID = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun routes_rateLimited() {
        withMockServer(dispatcher = RoutesDispatcher(responseCode = 429, responseBody = "{}")) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.routes(apiKey = "apikey", agencyID = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun routes_unauthorized() {
        withMockServer(
            dispatcher = RoutesDispatcher(
                responseCode = 401,
                responseBody = TRANSIT_LAND_UNAUTHORIZED_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.routes(apiKey = "apikey", agencyID = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun routes_minimal() {
        withMockServer(
            dispatcher = RoutesDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_ROUTES_MINIMAL_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.routes(apiKey = "apikey", agencyID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(20355691)
            assertThat(response.routes).contains(TRANSIT_LAND_ROUTE_1)
            assertThat(response.routes).contains(TRANSIT_LAND_ROUTE_2)
        }
    }

    @Test
    fun routes_success() {
        withMockServer(
            dispatcher = RoutesDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_ROUTES_SUCCESS_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.routes(apiKey = "apikey", agencyID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(20355691)
            assertThat(response.routes).contains(TRANSIT_LAND_ROUTE_1)
            assertThat(response.routes).contains(TRANSIT_LAND_ROUTE_2)
        }
    }

    @Test
    fun stops_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = StopsDispatcher(responseCode = 200, responseBody = "{}")
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.stops(apiKey = "apikey", agencyID = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun stops_rateLimited() {
        withMockServer(dispatcher = StopsDispatcher(responseCode = 429, responseBody = "{}")) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.stops(apiKey = "apikey", agencyID = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun stops_unauthorized() {
        withMockServer(
            dispatcher = StopsDispatcher(
                responseCode = 401,
                responseBody = TRANSIT_LAND_UNAUTHORIZED_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.stops(apiKey = "apikey", agencyID = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun stops_minimal() {
        withMockServer(
            dispatcher = StopsDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_STOPS_SUCCESS_MINIMAL_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.stops(apiKey = "apikey", agencyID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(439365585)
            assertThat(response.stops).contains(TRANSIT_LAND_STOP_1)
            assertThat(response.stops).contains(TRANSIT_LAND_STOP_2)
        }
    }

    @Test
    fun stops_success() {
        withMockServer(
            dispatcher = StopsDispatcher(
                responseCode = 200,
                responseBody = TRANSIT_LAND_STOPS_SUCCESS_FIXTURE
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response =
                subject.stops(apiKey = "apikey", agencyID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(439365585)
            assertThat(response.stops).contains(TRANSIT_LAND_STOP_1)
            assertThat(response.stops).contains(TRANSIT_LAND_STOP_2)
        }
    }

    private fun webClient(mockServer: MockWebServer): WebClient = WebClient.builder()
        .baseUrl(mockServer.url("/api/v2/rest/").toString())
        .build()

    inner class FeedDispatcher(private val responseCode: Int, private val resourcePath: String?) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val requestPath = request.path ?: throw IllegalArgumentException()
            require(requestPath.contains("/api/v2/rest/feeds.json"))
            val body =
                resourcePath?.let { resourceLoader.getResource("classpath:$it").file.readTextAndNormalize() } ?: "{}"
            return MockResponse().setResponseCode(responseCode).setBody(
                body
            ).setHeader("Content-Type", "application/json")
        }
    }

    class AgenciesDispatcher(private val responseCode: Int, private val responseBody: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: throw IllegalArgumentException()
            require(path.contains("/api/v2/rest/agencies.json"))
            return MockResponse().setResponseCode(responseCode).setBody(
                responseBody
            ).setHeader("Content-Type", "application/json")
        }
    }

    class RoutesDispatcher(private val responseCode: Int, private val responseBody: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: throw IllegalArgumentException()
            require(path.contains("/api/v2/rest/routes.json"))
            return MockResponse().setResponseCode(responseCode).setBody(
                responseBody
            ).setHeader("Content-Type", "application/json")
        }
    }

    class StopsDispatcher(private val responseCode: Int, private val responseBody: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: throw IllegalArgumentException()
            require(path.contains("/api/v2/rest/stops.json"))
            return MockResponse().setResponseCode(responseCode).setBody(
                responseBody
            ).setHeader("Content-Type", "application/json")
        }
    }
}
