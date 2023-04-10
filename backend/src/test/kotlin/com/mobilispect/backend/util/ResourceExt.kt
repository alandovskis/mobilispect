package com.mobilispect.backend.util

import org.springframework.core.io.ResourceLoader
import java.io.File
import java.nio.file.Path

/**
 * Copy resource from [src] to [dst].
 */
fun ResourceLoader.copyResourceTo(src: String, root: Path, dst: String): String {
    val inputStream = getResource(src).file.inputStream()
    val output = File(root.toFile(), dst)
    inputStream.copyTo(output.outputStream(), 1024)
    return output.path
}
