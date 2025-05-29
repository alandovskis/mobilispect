package com.mobilispect.mobile.data.route

import com.mobilispect.mobile.data.AppDatabase
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