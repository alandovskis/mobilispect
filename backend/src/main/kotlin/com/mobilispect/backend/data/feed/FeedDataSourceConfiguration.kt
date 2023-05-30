package com.mobilispect.backend.data.feed

import com.mobilispect.backend.data.transit_land.TransitLandFeedDataSource
import com.mobilispect.backend.data.transit_land.api.TransitLandAPI
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeedDataSourceConfiguration {
    @Bean
    fun feedDataSource(
        transitLandAPI: TransitLandAPI,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): FeedDataSource = TransitLandFeedDataSource(
        transitLandClient = transitLandAPI,
        transitLandCredentialsRepository = transitLandCredentialsRepository
    )
}
