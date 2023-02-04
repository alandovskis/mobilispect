package com.mobilispect.common.data.cloud

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine

@Module
@InstallIn(SingletonComponent::class)
object NetworkDataSourceProvidesModule {
    @Provides
    fun networkDataSource(): MobilispectAPINetworkDataSource {
        return MobilispectAPINetworkDataSource(
            httpEngine = OkHttpEngine(
                okHttpConfig() as OkHttpConfig
            )
        )
    }
}
