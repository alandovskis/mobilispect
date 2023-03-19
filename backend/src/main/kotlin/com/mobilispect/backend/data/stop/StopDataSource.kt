package com.mobilispect.backend.data.stop

import com.mobilispect.backend.data.api.PagingParameters

interface StopDataSource {
    fun stops(apiKey: String, agencyID: String, paging: PagingParameters = PagingParameters()): Result<StopResult>
}
