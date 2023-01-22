package com.mobilispect.common.data.cloud

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CloudNetworkDataSourceModule {
    @Binds
    fun networkDataSource(source: CloudNetworkDataSource): NetworkDataSource
}