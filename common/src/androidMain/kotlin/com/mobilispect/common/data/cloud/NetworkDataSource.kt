package com.mobilispect.common.data.cloud

import com.mobilispect.common.data.agency.Agency

interface NetworkDataSource {
    suspend fun agencies(): Collection<Agency>
}