package com.mobilispect.mobile.data

import android.content.Context
import com.mobilispect.common.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    internal fun db(@ApplicationContext context: Context) = AppDatabase.getInstance(context)
}