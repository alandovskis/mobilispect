package com.mobilispect.backend.data.download

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import java.nio.file.StandardOpenOption
import java.time.Duration

/**
 * A [Downloader] that uses Spring [WebClient].
 */
internal class WebClientDownloader(webClientBuilder: WebClient.Builder) : Downloader {
    private var webClient: WebClient = webClientBuilder.build()

    override fun download(url: String): Result<String> {
        val dest = kotlin.io.path.createTempFile()
        return try {
            val dataBuffer = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DataBuffer::class.java)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter { error ->
                        val responseError = error as? WebClientResponseException
                        val serverError = responseError?.statusCode?.is5xxServerError ?: false
                        val rateLimitedError =
                            responseError?.statusCode?.isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS) ?: false
                        return@filter serverError || rateLimitedError
                    })

            DataBufferUtils.write(
                dataBuffer, dest, StandardOpenOption.CREATE
            ).share().block()
            return Result.success(dest.toString())
        } catch (e: WebClientException) {
            Result.failure(e)
        }
    }
}
