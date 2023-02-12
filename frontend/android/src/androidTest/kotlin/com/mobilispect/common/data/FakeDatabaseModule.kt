package com.mobilispect.common.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [
        DatabaseModule::class
    ]
)
object FakeDatabaseModule {
    @Singleton
    @Provides
    internal fun db(@ApplicationContext context: Context) = Room.inMemoryDatabaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
    ).build()
}