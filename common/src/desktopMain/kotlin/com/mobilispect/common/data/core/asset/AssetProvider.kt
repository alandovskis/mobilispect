package com.mobilispect.common.data.core.asset

import java.io.InputStream

actual class AssetProvider {
    actual fun asset(name: String): Result<InputStream> {
        val inputStream = javaClass.getResourceAsStream(name)
            ?: return Result.failure(Exception("File $name was not found"))
        return Result.success(inputStream)
    }
}