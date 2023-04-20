package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.route.OneStopRouteIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class OneStopRouteIDDataSourceConfiguration {
    @Bean
    fun oneStopRouteID(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): OneStopRouteIDDataSource = TransitLandOneStopRouteIDDataSource(
        transitLandClient = transitLandClient,
        transitLandCredentialsRepository = transitLandCredentialsRepository
    )
}
