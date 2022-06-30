package com.mobilispect.common.data.core.asset

import java.io.InputStream

expect class AssetProvider {
    fun asset(name: String): Result<InputStream>
}