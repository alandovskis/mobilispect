package com.mobilispect.backend.data.stop

interface OneStopStopIDDataSource {
    fun stops(feedID: String): Result<StopIDMap>
}
