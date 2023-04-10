package com.mobilispect.backend.data.gtfs

import com.mobilispect.backend.data.schedule.ScheduledStopDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GTFSScheduledStopDataSourceConfiguration {
    @Bean
    fun scheduledStopDataSource(): ScheduledStopDataSource = GTFSScheduledStopDataSource()
}
