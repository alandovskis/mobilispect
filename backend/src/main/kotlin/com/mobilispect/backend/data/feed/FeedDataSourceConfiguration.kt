package com.mobilispect.backend.data.feed

import com.mobilispect.backend.data.transit_land.TransitLandFeedDataSource
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import com.mobilispect.backend.data.transit_land.internal.client.TransitLandClient
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
