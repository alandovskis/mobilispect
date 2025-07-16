package com.mobilispect.backend

import com.mobilispect.backend.schedule.gtfs.GTFSScheduledTripDataSource
import com.mobilispect.backend.schedule.route.RouteIDDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSScheduledTripDataSourceConfiguration {
    @Bean
    fun scheduledTripDataSource(routeIDDataSource: RouteIDDataSource): ScheduledTripDataSource =
        GTFSScheduledTripDataSource(routeIDDataSource)
}
