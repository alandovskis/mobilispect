package com.mobilispect.common.data.route

import com.mobilispect.common.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RouteDAModule {
    @Provides
    internal fun dao(db: AppDatabase) = db.routeDAO()
}