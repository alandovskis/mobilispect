package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.agency.AgencyDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("unused")
internal class GTFSAgencyDataSourceConfiguration {
    @Bean
    fun agencyDataSource(): AgencyDataSource = GTFSAgencyDataSource()
}
