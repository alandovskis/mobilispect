package com.mobilispect.backend.schedule.gtfs

import com.mobilispect.backend.GTFSScheduledStopDataSource
import com.mobilispect.backend.ScheduledStopDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GTFSScheduledStopDataSourceConfiguration {
    @Bean
    fun scheduledStopDataSource(): ScheduledStopDataSource = GTFSScheduledStopDataSource()
}
