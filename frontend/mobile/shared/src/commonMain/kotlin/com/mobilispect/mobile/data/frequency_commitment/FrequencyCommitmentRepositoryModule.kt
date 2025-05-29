package com.mobilispect.mobile.data.frequency_commitment

import com.mobilispect.mobile.data.frequency_commitment.DefaultFrequencyCommitmentRepository
import com.mobilispect.mobile.data.frequency_commitment.FrequencyCommitmentRepository
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