package com.mobilispect.backend.data.feed

import com.mobilispect.backend.data.transit_land.TransitLandClient
import com.mobilispect.backend.data.transit_land.TransitLandCredentialsRepository
import com.mobilispect.backend.data.transit_land.TransitLandFeedDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeedDataSourceConfiguration {
    @Bean
    fun feedDataSource(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): FeedDataSource = TransitLandFeedDataSource(
        transitLandClient = transitLandClient,
        transitLandCredentialsRepository = transitLandCredentialsRepository
    )
}
