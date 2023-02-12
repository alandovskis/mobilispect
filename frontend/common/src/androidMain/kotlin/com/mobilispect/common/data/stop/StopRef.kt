package com.mobilispect.common.data.stop

data class StopRef(val geohash: String, val name: String) {
    val id = "s-$geohash-$name"
}