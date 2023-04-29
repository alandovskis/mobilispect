package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.AgencyDataSource
import com.mobilispect.backend.data.transit_land.TransitLandAgencyIDDataSource
import com.mobilispect.backend.data.transit_land.TransitLandClient
import com.mobilispect.backend.data.transit_land.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("unused")
internal class GTFSAgencyDataSourceConfiguration {
    @Bean
    fun agencyDataSource(
        transitLandClient: TransitLandClient,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): AgencyDataSource =
        GTFSAgencyDataSource(
            TransitLandAgencyIDDataSource(transitLandClient, transitLandCredentialsRepository)
        )
}
