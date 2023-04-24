package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.OneStopRouteIDDataSource
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSScheduledTripDataSourceConfiguration {
    @Bean
    fun dataSource(routeIDDataSource: OneStopRouteIDDataSource): ScheduledTripDataSource =
        GTFSScheduledTripDataSource(routeIDDataSource)
}
