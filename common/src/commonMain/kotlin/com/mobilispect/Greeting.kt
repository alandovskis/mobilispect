package com.mobilispect

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}