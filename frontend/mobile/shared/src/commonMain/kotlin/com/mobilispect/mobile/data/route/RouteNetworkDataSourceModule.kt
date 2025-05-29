package com.mobilispect.mobile.data.route

import com.mobilispect.common.data.route.RouteNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RouteNetworkDataSourceModule {
    @Binds
    fun dataSource(repository: TransitLandRouteDataSource): RouteNetworkDataSource
}