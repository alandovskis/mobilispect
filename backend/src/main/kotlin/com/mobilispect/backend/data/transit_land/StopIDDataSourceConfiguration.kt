package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.stop.StopIDDataSource
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import com.mobilispect.backend.data.transit_land.internal.client.TransitLandClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class StopIDDataSourceConfiguration {
    @Bean
    fun oneStopStopIDDataSource(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): StopIDDataSource = TransitLandStopIDDataSource(transitLandClient, transitLandCredentialsRepository)
}
