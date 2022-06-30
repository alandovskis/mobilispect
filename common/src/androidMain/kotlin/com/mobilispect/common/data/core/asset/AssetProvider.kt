package com.mobilispect.common.data.core.asset

import android.content.res.AssetManager
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

actual class AssetProvider @Inject constructor(private val assetManager: AssetManager) {
    actual fun asset(name: String): Result<InputStream> =
        try {
            Result.success(assetManager.open(name))
        } catch (e: IOException) {
            Result.failure(e)
        }
}