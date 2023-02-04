package com.mobilispect.common.data.cloud

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [NetworkDataSourceBindsModule::class]
)
interface FakeNetworkDataSourceModule {
    @Binds
    fun networkDataSource(source: FakeNetworkDataSource): NetworkDataSource
}