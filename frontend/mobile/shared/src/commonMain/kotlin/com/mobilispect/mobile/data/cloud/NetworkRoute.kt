package com.mobilispect.mobile.data.cloud

@kotlinx.serialization.Serializable
data class NetworkRoute(
    val shortName: String,
    val longName: String,
    val agencyID: String,
    private val _links: Links,
) {
    val id = _links.self.href.split("/").last()
}