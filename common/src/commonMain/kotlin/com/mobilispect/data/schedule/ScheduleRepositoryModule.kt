package com.mobilispect.data.schedule

import com.mobilispect.data.frequency.FakeScheduleRepository
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