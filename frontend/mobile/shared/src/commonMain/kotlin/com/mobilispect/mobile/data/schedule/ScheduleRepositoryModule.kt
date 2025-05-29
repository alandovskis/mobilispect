package com.mobilispect.mobile.data.schedule

import com.mobilispect.common.data.schedule.FakeScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ScheduleRepositoryModule {
    @Binds
    fun repo(repo: FakeScheduleRepository): ScheduleRepository
}