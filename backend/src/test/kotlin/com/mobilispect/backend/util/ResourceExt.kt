package com.mobilispect.backend.util

import org.springframework.core.io.ResourceLoader
import java.io.File
import java.nio.file.Path

fun ResourceLoader.copyResourceTo(src: String, root: Path, dst: String) {
    val inputStream = getResource(src).file.inputStream()
    val outputStream = File(root.toFile(), dst).outputStream()
    inputStream.copyTo(outputStream, 1024)
}