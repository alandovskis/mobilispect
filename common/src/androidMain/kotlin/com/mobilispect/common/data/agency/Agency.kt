package com.mobilispect.common.data.agency

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mobilispect.common.data.cloud.NetworkAgency

@Entity(tableName = "agencies")
data class Agency(
    @PrimaryKey
    val ref: AgencyRef,

    val name: String
)

fun NetworkAgency.asEntity(): Agency =
    Agency(ref = ref, name = name)

