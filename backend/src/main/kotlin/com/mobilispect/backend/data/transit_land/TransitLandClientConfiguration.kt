package com.mobilispect.backend.data.transit_land

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

private const val CONNECT_TIMEOUT_ms = 5_000

@Configuration
@Suppress("Unused")
internal class TransitLandClientConfiguration {
    private val logger = LoggerFactory.getLogger(TransitLandClient::class.java)

    @Bean
    fun webclient(builder: WebClient.Builder): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_ms)
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(5))
                connection.addHandlerLast(WriteTimeoutHandler(5))
            }
            .doOnRequest { request, _ ->
                logger.trace(
                    "${
                        request.method().name()
                    } ${request.uri()} ${request.fullPath()} ->"
                )
            }
            .doOnResponse { response, _ ->
                logger.trace(
                    "<- {}: {} {}",
                    response.uri(),
                    response.status().codeAsText(),
                    response.status().reasonPhrase()
                )
            }

        return builder
            .baseUrl("https://transit.land/api/v2/rest")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}
