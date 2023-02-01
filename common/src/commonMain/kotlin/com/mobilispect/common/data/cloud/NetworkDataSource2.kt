package com.mobilispect.common.data.cloud

interface NetworkDataSource2 {
    suspend fun agencies(): Collection<NetworkAgency>
}