package com.mobilispect.api

/**
 * A [String] that is guaranteed to not be blank.
 */
@Suppress("CanBeParameter")
@JvmInline value class NonBlankString(private val value: String) {
    init {
        check(value.isNotBlank())
    }
}