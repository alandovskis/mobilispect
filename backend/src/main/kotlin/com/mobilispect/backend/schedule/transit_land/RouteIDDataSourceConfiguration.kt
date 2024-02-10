package com.mobilispect.backend.schedule.transit_land

import com.mobilispect.backend.schedule.route.RouteIDDataSource
import com.mobilispect.backend.schedule.transit_land.api.TransitLandAPI
import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class RouteIDDataSourceConfiguration {
    @Bean
    fun oneStopRouteID(
        transitLandAPI: TransitLandAPI,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): RouteIDDataSource = TransitLandRouteIDDataSource(
        transitLandAPI = transitLandAPI,
        transitLandCredentialsRepository = transitLandCredentialsRepository
    )
}
