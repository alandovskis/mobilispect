package com.mobilispect.backend.util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer

fun withMockServer(dispatcher: Dispatcher, block: (MockWebServer) -> Unit) {
    val mockServer = MockWebServer()
    mockServer.dispatcher = dispatcher
    mockServer.start()
    block(mockServer)
    mockServer.shutdown()
}
