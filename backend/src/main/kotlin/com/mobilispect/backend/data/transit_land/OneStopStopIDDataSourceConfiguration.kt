package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.stop.OneStopStopIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OneStopStopIDDataSourceConfiguration {
    @Bean
    fun oneStopStopIDDataSource(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): OneStopStopIDDataSource = TransitLandOneStopStopIDDataSource(transitLandClient, transitLandCredentialsRepository)
}
