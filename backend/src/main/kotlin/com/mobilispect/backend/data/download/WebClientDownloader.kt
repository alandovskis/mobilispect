package com.mobilispect.backend.data.download

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import java.nio.file.StandardOpenOption

/**
 * A [Downloader] that uses Spring [WebClient].
 */
internal class WebClientDownloader(webClientBuilder: WebClient.Builder) : Downloader {
    private var webClient: WebClient = webClientBuilder.build()

    override fun download(url: String): Result<String> {
        val dest = kotlin.io.path.createTempFile()
        return try {
            val dataBuffer = webClient.get().uri(url).retrieve().bodyToFlux(DataBuffer::class.java)
            DataBufferUtils.write(
                dataBuffer, dest, StandardOpenOption.CREATE
            ).share().block()
            Result.success(dest.toString())
        } catch (e: WebClientRequestException) {
            Result.failure(e)
        }
    }
}
