package com.mobilispect.backend.data.agency

import org.springframework.data.mongodb.core.mapping.Document

@Suppress("ConstructorParameterNaming") // For _id
@Document(value = "agencies")
class Agency(
    /**
     * ID (in OneStop ID format ex: o-geohash-name)
     */
    val _id: String,
    val name: String,
    val version: String
) {
    constructor(
        id: OneStopAgencyID,
        name: String,
        version: String
    ) : this(_id = id.str, name = name, version = version)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Agency

        if (_id != other._id) return false
        if (name != other.name) return false
        return version == other.version
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }

    override fun toString(): String {
        return "Agency(_id='$_id', name='$name', version='$version')"
    }
}
