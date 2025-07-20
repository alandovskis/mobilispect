package com.mobilispect.backend.util

import org.springframework.core.io.ResourceLoader
import java.nio.file.Path

/**
 * Copy resource from [src] to [dst].
 */
fun ResourceLoader.copyResourceTo(src: String, root: Path, dst: String): Path {
    val inputStream = getResource(src).file.inputStream()
    val output = root.resolve(dst)
    inputStream.copyTo(output.toFile().outputStream(), 1024)
    return output
}
