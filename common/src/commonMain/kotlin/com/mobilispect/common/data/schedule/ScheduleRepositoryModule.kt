package com.mobilispect.common.data.schedule

import com.mobilispect.common.data.frequency.FakeScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ScheduleRepositoryModule {
    @Binds
    fun repo(repo: FakeScheduleRepository): ScheduleRepository
}