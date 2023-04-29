package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.route.RouteIDDataSource
import com.mobilispect.backend.data.schedule.ScheduledTripDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class GTFSScheduledTripDataSourceConfiguration {
    @Bean
    fun dataSource(routeIDDataSource: RouteIDDataSource): ScheduledTripDataSource =
        GTFSScheduledTripDataSource(routeIDDataSource)
}
