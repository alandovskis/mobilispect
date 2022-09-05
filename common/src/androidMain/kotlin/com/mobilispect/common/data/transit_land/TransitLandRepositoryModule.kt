package com.mobilispect.common.data.transit_land

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TransitLandRepositoryModule {
    @Binds
    fun repository(repo: DefaultTransitLandConfigRepository): TransitLandConfigRepository
}