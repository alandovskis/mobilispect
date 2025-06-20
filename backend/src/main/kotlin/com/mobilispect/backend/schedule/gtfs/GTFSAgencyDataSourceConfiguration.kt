package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.agency.AgencyDataSource
import com.mobilispect.backend.schedule.transit_land.TransitLandAgencyIDDataSource
import com.mobilispect.backend.schedule.transit_land.api.TransitLandAPI
import com.mobilispect.backend.schedule.transit_land.api.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("unused")
internal class GTFSAgencyDataSourceConfiguration {
    @Bean
    fun agencyDataSource(
        transitLandAPI: TransitLandAPI,
        transitLandCredentialsRepository: TransitLandCredentialsRepository
    ): AgencyDataSource =
        GTFSAgencyDataSource(
            TransitLandAgencyIDDataSource(transitLandAPI, transitLandCredentialsRepository)
        )
}
