package com.mobilispect.android.data.routes

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