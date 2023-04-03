package com.mobilispect.backend.util

import java.io.File

/**
 * Ensure that newlines are normalized to Unix-style.
 */
fun File.readTextAndNormalize(): String = readText().replace("\r\n", "\n")