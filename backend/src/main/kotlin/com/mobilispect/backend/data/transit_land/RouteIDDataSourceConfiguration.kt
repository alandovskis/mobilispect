package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.route.RouteIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class RouteIDDataSourceConfiguration {
    @Bean
    fun oneStopRouteID(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): RouteIDDataSource = TransitLandRouteIDDataSource(
        transitLandClient = transitLandClient,
        transitLandCredentialsRepository = transitLandCredentialsRepository
    )
}
