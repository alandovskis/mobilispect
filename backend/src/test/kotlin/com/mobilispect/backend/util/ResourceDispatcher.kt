package com.mobilispect.backend.util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * A [Dispatcher] that uses resources as source.
 */
class ResourceDispatcher : Dispatcher() {
    private val urls = mutableMapOf<String, MockResponse>()
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path?.split("?")?.firstOrNull() ?: throw IllegalArgumentException()
        require(urls.contains(path))
        return urls[path] ?: MockResponse()
    }

    fun returningResponseFor(url: String, responseCode: Int, resource: String?): Dispatcher {
        val body = if (resource != null) {
            val buffer = Buffer()
            buffer.writeAll(FileSystem.RESOURCES.source(resource.toPath()))
            buffer
        } else {
            Buffer()
        }

        val response = MockResponse().setResponseCode(responseCode)
            .setBody(body)
            .setHeader("Content-Type", "application/json")
        urls[url] = response
        return this
    }
}
