package com.mobilispect.backend.data.agency

import com.mobilispect.backend.data.transit_land.TransitLandClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class RegionalAgencyDataSourceConfiguration {
    @Bean
    fun source(webClient: WebClient) = TransitLandClient(webClient = webClient)
}
