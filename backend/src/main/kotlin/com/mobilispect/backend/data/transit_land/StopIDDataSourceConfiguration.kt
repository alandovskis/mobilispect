package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.stop.StopIDDataSource
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
