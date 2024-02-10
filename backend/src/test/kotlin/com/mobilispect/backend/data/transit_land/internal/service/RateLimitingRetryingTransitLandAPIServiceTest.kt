package com.mobilispect.backend.schedule.transit_land.internal.service

import com.mobilispect.backend.schedule.transit_land.internal.client.*
import com.mobilispect.backend.util.ResourceDispatcher
import com.mobilispect.backend.util.withMockServer
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class RateLimitingRetryingTransitLandAPIServiceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun feedIsRateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher().returningResponseFor(
                url = FEEDS_URL, responseCode = 200, resource = "transit-land/feed/full.json"
            )
        ) { mockServer ->
            val subject = RateLimitingRetryingTransitLandAPIService(
                TransitLandClient(
                    builder = WebClient.builder(), baseURL = mockServer.url("/api/v2/rest/").toString()
                ),
            )

            tryUntilRateLimited {
                subject.feed(apiKey = "API_KEY", feedID = "FEED_ID")
            }
        }
    }

    @Test
    fun agenciesIsRateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher().returningResponseFor(
                url = AGENCIES_URL, responseCode = 200, resource = "transit-land/feeds-for/full.json"
            )
        ) { mockServer ->
            val subject = RateLimitingRetryingTransitLandAPIService(
                TransitLandClient(
                    builder = WebClient.builder(), baseURL = mockServer.url("/api/v2/rest/").toString()
                ),
            )

            tryUntilRateLimited {
                subject.agencies(apiKey = "API_KEY", region = "REGION")
            }
        }
    }

    @Test
    fun routesIsRateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher().returningResponseFor(
                url = ROUTES_URL, responseCode = 200, resource = "transit-land/routes/full.json"
            )
        ) { mockServer ->
            val subject = RateLimitingRetryingTransitLandAPIService(
                TransitLandClient(
                    builder = WebClient.builder(), baseURL = mockServer.url("/api/v2/rest/").toString()
                ),
            )

            tryUntilRateLimited {
                subject.routes(apiKey = "API_KEY", feedID = "FEED_ID")
            }
        }
    }

    @Test
    fun stopsIsRateLimited() {
        withMockServer(
            dispatcher = ResourceDispatcher().returningResponseFor(
                url = STOPS_URL, responseCode = 200, resource = "transit-land/stops/full.json"
            )
        ) { mockServer ->
            val subject = RateLimitingRetryingTransitLandAPIService(
                TransitLandClient(
                    builder = WebClient.builder(), baseURL = mockServer.url("/api/v2/rest/").toString()
                ),
            )

            tryUntilRateLimited {
                subject.stops(apiKey = "API_KEY", feedID = "FEED_ID")
            }
        }
    }

    private fun tryUntilRateLimited(function: (Int) -> Unit) {
        assertThrows<RequestNotPermitted> {
            repeat(61, function)
        }
    }
}