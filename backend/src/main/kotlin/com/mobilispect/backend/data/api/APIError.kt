package com.mobilispect.backend.data.api

sealed interface APIError
class NetworkError(exception: Exception) : Exception(exception), APIError
object TooManyRequests : Exception("Too many requests"), APIError

object Unauthorized : Exception("Unauthorized"), APIError

class GenericError(exception: String) : Exception(exception), APIError
