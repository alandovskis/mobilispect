package com.mobilispect.common.data.cloud

sealed interface APIError
object NetworkError : APIError, Exception()
