package com.mobilispect.common.data.cloud

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkDataSourceBindsModule {
    @Binds
    fun networkDataSource(source: MobilispectAPINetworkDataSource): NetworkDataSource
}