package com.mobilispect.common.data.cloud

import com.mobilispect.common.testing.AGENCIES_SUCCESSFUL_FIXTURE
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [NetworkDataSourceProvidesModule::class]
)
internal object FakeNetworkDataSourceModule {
    @Provides
    fun networkDataSource(): MobilispectAPINetworkDataSource {
        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/agencies" -> {
                    respond(
                        content = ByteReadChannel(
                            AGENCIES_SUCCESSFUL_FIXTURE.trimIndent()
                        ),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> throw IllegalArgumentException("Unexpected URL: ${request.url.encodedPath}")
            }
        }

        return MobilispectAPINetworkDataSource(mockEngine)
    }
}