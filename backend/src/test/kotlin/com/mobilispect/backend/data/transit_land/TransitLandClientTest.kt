package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.AgencyResultItem
import com.mobilispect.backend.data.api.NetworkError
import com.mobilispect.backend.data.api.TooManyRequests
import com.mobilispect.backend.data.api.Unauthorized
import com.mobilispect.backend.data.feed.Feed
import com.mobilispect.backend.data.feed.FeedVersion
import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.data.route.FeedLocalRouteID
import com.mobilispect.backend.data.route.OneStopRouteID
import com.mobilispect.backend.data.route.RouteResultItem
import com.mobilispect.backend.data.stop.StopResultItem
import com.mobilispect.backend.util.ResourceDispatcher
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
import java.time.LocalDate

private const val AGENCIES_URL = "/api/v2/rest/agencies.json"
private const val ROUTES_URL = "/api/v2/rest/routes.json"
private const val STOPS_URL = "/api/v2/rest/stops.json"

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
    fun feed_success() {
        withMockServer(
            dispatcher = FeedDispatcher(
                responseCode = 200, resourcePath = "transit-land/feed/full.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.feed(apiKey = "apikey", feedID = "f-f25f-rseaudetransportdelongueuil").getOrNull()!!

            assertThat(response).isEqualTo(
                VersionedFeed(
                    feed = Feed(
                        _id = "f-f25f-rseaudetransportdelongueuil",
                        url = "https://www.rtl-longueuil.qc.ca/transit/latestfeed/RTL.zip"
                    ), version = FeedVersion(
                        _id = "41c3e41b979db2e58f9deeb98f8f91be47f3ba17",
                        feedID = "f-f25f-rseaudetransportdelongueuil",
                        startsOn = LocalDate.of(2023, 6, 26),
                        endsOn = LocalDate.of(2023, 8, 20)
                    )
                )
            )
        }
    }

    @Test
    fun agencies_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
            url = AGENCIES_URL, responseCode = 200, resource = null
        )
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.agencies(apiKey = "apikey", region = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun agencies_rateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                url = AGENCIES_URL, responseCode = 429, resource = null
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", region = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun agencies_unauthorized() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                url = AGENCIES_URL, responseCode = 401, resource = "transit-land/common/unauthorized.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", region = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun agencies_success() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                url = AGENCIES_URL, responseCode = 200, resource = "transit-land/feeds-for/full.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.agencies(apiKey = "apikey", region = "city").getOrNull()!!

            assertThat(response.after).isEqualTo(3973)
            assertThat(response.agencies).contains(
                AgencyResultItem(
                    id = "o-sr7f3-mtmmobilitaetrasportimolfetta",
                    name = "MTM Mobilita' e Trasporti Molfetta",
                    version = "d043eb31ed57955e134917efbcd8912ccacd74d6",
                    feedID = "f-sr7f3-mtmmobilitaetrasportimolfetta",
                    agencyID = "MTM"
                )
            )
            assertThat(response.agencies).contains(
                AgencyResultItem(
                    id = "o-9xhvw-townofestespark",
                    name = "Town of Estes Park",
                    version = "913b07b945e2b2afbc5d37d7bcd883918933c9ce",
                    feedID = "f-9xhvw-townofestespark",
                    agencyID = "EPS"
                )
            )
        }
    }

    @Test
    fun routes_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
            url = ROUTES_URL, responseCode = 200, resource = null
        )
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.routes(apiKey = "apikey", feedID = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun routes_rateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                url = ROUTES_URL, responseCode = 429, resource = "transit-land/common/rate-limited.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.routes(apiKey = "apikey", feedID = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun routes_unauthorized() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                ROUTES_URL,
                401,
                "transit-land/common/unauthorized.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.routes(apiKey = "apikey", feedID = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun routes_success() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                url = ROUTES_URL, responseCode = 200, resource = "transit-land/routes/full.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.routes(apiKey = "apikey", feedID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(20355691)
            assertThat(response.routes).contains(
                RouteResultItem(
                    id = OneStopRouteID("r-f25d-1"),
                    routeID = FeedLocalRouteID("1"),
                    agencyID = "STM",
                )
            )
            assertThat(response.routes).contains(
                RouteResultItem(
                    id = OneStopRouteID("r-f25d-2"),
                    routeID = FeedLocalRouteID("2"),
                    agencyID = "STM",
                )
            )
        }
    }

    @Test
    fun stops_networkError() {
        val mockServer = MockWebServer()
        mockServer.dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
            url = STOPS_URL,
            responseCode = 200,
            resource = null
        )
        mockServer.start()
        val webClient = webClient(mockServer)
        mockServer.shutdown()

        subject = TransitLandClient(webClient)
        val result = subject.stops(apiKey = "apikey", feedID = "city")

        assertThat(result.exceptionOrNull()).isInstanceOf(NetworkError::class.java)
    }

    @Test
    fun stops_rateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                STOPS_URL, responseCode = 429, resource = null
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.stops(apiKey = "apikey", feedID = "city").exceptionOrNull()

            assertThat(response).isInstanceOf(TooManyRequests::class.java)
        }
    }

    @Test
    fun stops_unauthorized() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                STOPS_URL, responseCode = 401, resource = "transit-land/common/unauthorized.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.stops(apiKey = "apikey", feedID = "city").exceptionOrNull()!!

            assertThat(response).isInstanceOf(Unauthorized::class.java)
        }
    }

    @Test
    fun stops_success() {
        withMockServer(
            dispatcher = ResourceDispatcher(resourceLoader).returningResponseFor(
                STOPS_URL, responseCode = 200, resource = "transit-land/stops/full.json"
            )
        ) { mockServer ->
            val webClient = webClient(mockServer)

            subject = TransitLandClient(webClient)
            val response = subject.stops(apiKey = "apikey", feedID = "o-f25d-socitdetransportdemontral").getOrNull()!!

            assertThat(response.after).isEqualTo(439365585)
            assertThat(response.stops).contains(
                StopResultItem(
                    id = "s-f25dt17bg5-stationangrignon",
                    stopID = "43",
                )
            )
            assertThat(response.stops).contains(
                StopResultItem(
                    id = "s-f25dt65h1j-stationmonk",
                    stopID = "42",
                )
            )
        }
    }

    private fun webClient(mockServer: MockWebServer): WebClient =
        WebClient.builder().baseUrl(mockServer.url("/api/v2/rest/").toString()).build()

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
}
