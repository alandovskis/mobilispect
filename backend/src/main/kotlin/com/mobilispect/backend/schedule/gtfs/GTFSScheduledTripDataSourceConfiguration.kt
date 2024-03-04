package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.schedule.route.RouteIDDataSource
import com.mobilispect.backend.schedule.schedule.ScheduledTripDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSScheduledTripDataSourceConfiguration {
    @Bean
    fun scheduledTripDataSource(routeIDDataSource: RouteIDDataSource): ScheduledTripDataSource =
        GTFSScheduledTripDataSource(routeIDDataSource)
}
