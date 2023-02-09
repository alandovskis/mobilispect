package com.mobilispect.common.data.routes

import com.mobilispect.common.data.route.OfflineFirstRouteRepository
import com.mobilispect.common.data.route.RouteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RouteRepositoryModule {
    @Binds
    fun repository(repository: OfflineFirstRouteRepository): RouteRepository
}