package com.mobilispect.common.data.agency

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobilispect.common.data.cloud.NetworkAgency

@Entity(tableName = "agencies")
data class Agency(
    @PrimaryKey
    val id: String,

    val name: String
)

fun NetworkAgency.asEntity(): Agency {
    return Agency(
        id = id, name = name
    )
}

