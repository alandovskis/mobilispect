package com.mobilispect.common.data.agency

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agencies")
data class Agency(
    @PrimaryKey
    val ref: AgencyRef,

    val name: String
)

