package com.mobilispect.common.data.routes

import com.mobilispect.common.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RouteDAOModule {
    @Provides
    fun dao(db: AppDatabase) = db.routeDAO()
}