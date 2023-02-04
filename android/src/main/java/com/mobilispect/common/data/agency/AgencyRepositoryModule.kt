package com.mobilispect.common.data.agency

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AgencyRepositoryModule {
    @Binds
    fun repo(repo: OfflineFirstAgencyRepository): AgencyRepository
}
