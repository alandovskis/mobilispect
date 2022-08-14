package com.mobilispect.common.data.frequency_commitment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FrequencyCommitmentRepositoryModule {
    @Binds
    fun repository(repo: DefaultFrequencyCommitmentRepository): FrequencyCommitmentRepository
}