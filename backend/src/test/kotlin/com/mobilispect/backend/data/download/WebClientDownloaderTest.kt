package com.mobilispect.backend.schedule.download

import com.mobilispect.backend.util.ResourceDispatcher
import com.mobilispect.backend.util.withMockServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

private const val DOWNLOAD_URL = "/download"

class WebClientDownloaderTest {
    @Test
    fun includesHeadersIfProvided() {
        withMockServer(
            dispatcher = ResourceDispatcher().returningResponseFor(
                url = DOWNLOAD_URL, responseCode = 200, resource = "gtfs-rt/stm.pb"
            )
        ) { mockServer ->
            val downloader = WebClientDownloader(WebClient.builder())
            val request = DownloadRequest(
                url = mockServer.url(DOWNLOAD_URL).toString(),
                headers = mapOf("X-Custom" to "raw")
            )

            val result = downloader.download(request)

            assertThat(result.isSuccess).isTrue()
            assertThat(mockServer.takeRequest().headers.contains("X-Custom" to "raw")).isTrue()
        }
    }
}