package com.mobilispect.common.data.agency

import com.mobilispect.mobile.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AgencyDAOModule {
    @Provides
    fun dao(db: AppDatabase) = db.agencyDAO()
}