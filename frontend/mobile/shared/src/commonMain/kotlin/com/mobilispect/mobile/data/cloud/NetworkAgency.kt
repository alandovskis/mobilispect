package com.mobilispect.common.data.cloud

@kotlinx.serialization.Serializable
class NetworkAgency(
    val name: String,
    val _links: Links,
) {
    val id = _links.self.href.split("/").last()
}
