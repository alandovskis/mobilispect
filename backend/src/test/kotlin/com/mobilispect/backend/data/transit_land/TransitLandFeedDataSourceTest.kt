package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.feed.VersionedFeed
import com.mobilispect.backend.util.readTextAndNormalize
import com.mobilispect.backend.util.withMockServer
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

@SpringBootTest
class TransitLandFeedDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun imports() {
        withMockServer(
            TestDispatcher(
                feedsForResource = "transit-land/feeds-for/full.json",
                feedResource = "transit-land/feed/full.json"
            )
        ) { mockServer ->
            val client = WebClient.builder()
                .baseUrl(mockServer.url("/api/v2/rest/").toString())
                .build()
            val transitLandClient = TransitLandClient(client)
            val subject = TransitLandFeedDataSource(
                transitLandClient = transitLandClient,
                transitLandCredentialsRepository = FakeTransitLandCredentialsRepository()
            )

            val feeds = subject.feeds("Montréal")
                .mapNotNull { feedRes -> feedRes.getOrNull() }

            assertThat(feeds).contains(
                VersionedFeed(
                    feed = com.mobilispect.backend.data.feed.Feed(
                        _id = "f-sr7f3-mtmmobilitaetrasportimolfetta",
                        url = "https://www.rtl-longueuil.qc.ca/transit/latestfeed/RTL.zip"
                    ),
                    version = com.mobilispect.backend.data.feed.FeedVersion(
                        _id = "41c3e41b979db2e58f9deeb98f8f91be47f3ba17",
                        feedID = "f-sr7f3-mtmmobilitaetrasportimolfetta",
                        startsOn = LocalDate.of(2023, 6, 26),
                        endsOn = LocalDate.of(2023, 8, 20)
                    )
                )
            )
            assertThat(feeds).contains(
                VersionedFeed(
                    feed = com.mobilispect.backend.data.feed.Feed(
                        _id = "f-9xhvw-townofestespark",
                        url = "https://www.rtl-longueuil.qc.ca/transit/latestfeed/RTL.zip"
                    ),
                    version = com.mobilispect.backend.data.feed.FeedVersion(
                        _id = "41c3e41b979db2e58f9deeb98f8f91be47f3ba17", feedID = "f-9xhvw-townofestespark",
                        startsOn = LocalDate.of(2023, 6, 26),
                        endsOn = LocalDate.of(2023, 8, 20)
                    )
                )
            )
        }
    }

    inner class TestDispatcher(private val feedsForResource: String, private val feedResource: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val requestPath = request.path ?: throw IllegalArgumentException()
            if (requestPath.contains("agencies.json")) {
                val body = resourceLoader.getResource("classpath:$feedsForResource").file.readTextAndNormalize()
                return MockResponse().setResponseCode(200)
                    .setBody(body)
                    .setHeader("Content-type", "application/json")
            } else if (requestPath.contains("feeds.json")) {
                val body = resourceLoader.getResource("classpath:$feedResource").file.readTextAndNormalize()
                return MockResponse().setResponseCode(200)
                    .setBody(body)
                    .setHeader("Content-type", "application/json")
            }
            throw IllegalArgumentException(requestPath)
        }
    }
}