package com.mobilispect.backend

object TooManyRequests : Throwable() {
    private fun readResolve(): Any = TooManyRequests

}
