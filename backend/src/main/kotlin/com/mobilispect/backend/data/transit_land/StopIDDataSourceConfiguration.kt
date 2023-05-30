package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.stop.StopIDDataSource
import com.mobilispect.backend.data.transit_land.api.TransitLandAPI
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class StopIDDataSourceConfiguration {
    @Bean
    fun oneStopStopIDDataSource(
        transitLandAPI: TransitLandAPI,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): StopIDDataSource = TransitLandStopIDDataSource(transitLandAPI, transitLandCredentialsRepository)
}
