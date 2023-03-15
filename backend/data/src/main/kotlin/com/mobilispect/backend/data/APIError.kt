package com.mobilispect.backend.data

sealed interface APIError
class NetworkError(exception: Exception) : Exception(exception), APIError
object TooManyRequests : Exception("Too many requests"), APIError

class GenericError(exception: String) : Exception(exception), APIError