package com.mobilispect.backend.data.transit_land

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

@SpringBootTest
class TransitLandFeedDataSourceTest {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @Test
    fun imports() {
        withMockServer(
            TestDispatcher(
                agenciesResource = "transit-land/agencies/full.json",
                feedResource = "transit-land/feed/full.json"
            )
        ) { mockServer ->
            val client = WebClient.builder()
                .baseUrl(mockServer.url("/api/v2/rest/").toString())
                .build()
            val transitLandClient = TransitLandClient(client)
            val subject = TransitLandFeedDataSource(
                transitLandClient = transitLandClient,
                transitLandCredentialsRepository = DummyTransitLandCredentialsRepository()
            )

            val feeds = subject.feeds("Montréal").getOrNull()!!

            assertThat(feeds).contains(TRANSIT_LAND_FEED_FIXTURE)
        }
    }

    inner class TestDispatcher(private val agenciesResource: String, private val feedResource: String) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val requestPath = request.path ?: throw IllegalArgumentException()
            if (requestPath.contains("agencies.json")) {
                val body = resourceLoader.getResource("classpath:$agenciesResource").file.readTextAndNormalize()
                return MockResponse().setResponseCode(200)
                    .setBody(body)
            } else if (requestPath.contains("feeds.json")) {
                val body = resourceLoader.getResource("classpath:$feedResource").file.readTextAndNormalize()
                return MockResponse().setResponseCode(200)
                    .setBody(body)
            }
            throw IllegalArgumentException(requestPath)
        }
    }
}