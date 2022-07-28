package com.mobilispect.data.asset

import android.content.Context
import android.content.res.AssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.JsonNull.content

@Module
@InstallIn(SingletonComponent::class)
object AssetProviderModule {
    @Provides
    fun assetManager(@ApplicationContext context: Context): AssetManager =
        context.assets
}