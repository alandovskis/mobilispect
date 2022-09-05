package com.mobilispect.common.data.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey
    val id: RouteRef,

    @ColumnInfo(name = "short_name")
    val shortName: String,

    @ColumnInfo(name = "long_name")
    val longName: String,
)