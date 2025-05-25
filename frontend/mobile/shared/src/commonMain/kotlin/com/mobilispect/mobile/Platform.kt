package com.mobilispect.mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform