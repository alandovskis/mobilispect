package com.mobilispect.backend.data.transit_land

import com.mobilispect.backend.data.agency.AgencyIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class AgencyIDDataSourceConfiguration {
    @Bean
    fun oneStopAgencyIDDataSource(
        client: TransitLandClient,
        credentialRepository: TransitLandCredentialsRepository
    ): AgencyIDDataSource = TransitLandAgencyIDDataSource(client, credentialRepository)
}
