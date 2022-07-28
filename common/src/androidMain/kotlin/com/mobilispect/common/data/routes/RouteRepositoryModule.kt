package com.mobilispect.common.data.routes

import com.mobilispect.data.routes.DefaultRouteRepository
import com.mobilispect.data.routes.RouteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RouteRepositoryModule {
    @Binds
    fun repository(repository: DefaultRouteRepository): RouteRepository
}