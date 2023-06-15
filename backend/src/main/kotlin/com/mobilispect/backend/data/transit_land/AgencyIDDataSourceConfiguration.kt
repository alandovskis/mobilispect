package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.AgencyIDDataSource
import com.mobilispect.backend.data.transit_land.api.TransitLandAPI
import com.mobilispect.backend.data.transit_land.api.TransitLandCredentialsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class AgencyIDDataSourceConfiguration {
    @Bean
    fun oneStopAgencyIDDataSource(
        transitLandAPI: TransitLandAPI,
        credentialRepository: TransitLandCredentialsRepository
    ): AgencyIDDataSource = TransitLandAgencyIDDataSource(transitLandAPI, credentialRepository)
}
