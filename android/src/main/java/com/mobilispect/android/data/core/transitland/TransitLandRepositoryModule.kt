package com.mobilispect.android.data.core.transitland

import com.mobilispect.common.data.core.transitland.DefaultTransitLandConfigRepository
import com.mobilispect.common.data.core.transitland.TransitLandConfigRepository
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