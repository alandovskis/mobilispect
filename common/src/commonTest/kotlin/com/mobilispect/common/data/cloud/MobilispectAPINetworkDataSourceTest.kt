package com.mobilispect.common.data.cloud

import com.mobilispect.common.testing.AGENCIES_SUCCESSFUL_FIXTURE
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MobilispectAPINetworkDataSourceTest {
    @Test
    fun agencies() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals("/agencies", request.url.encodedPath, null)
            respond(
                content = ByteReadChannel(
                    AGENCIES_SUCCESSFUL_FIXTURE.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val networkDataSource = MobilispectAPINetworkDataSource(mockEngine)

        val actual = networkDataSource.agencies()

        with(actual.first()) {
            assertEquals("http://localhost:49336/agencies/o-abcd-a", _links.self.href)
            assertEquals("A", name)
        }
        with(actual.last()) {
            assertEquals("http://localhost:49336/agencies/o-abcd-b", _links.self.href)
            assertEquals("B", name)
        }
    }
}