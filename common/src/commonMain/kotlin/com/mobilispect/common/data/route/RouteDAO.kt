package com.mobilispect.common.data.route

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
internal interface RouteDAO {
    @Insert
    suspend fun insert(route: Route)

    @Query("SELECT * FROM routes WHERE id = :ref")
    suspend fun withRef(ref: RouteRef): Route?
}
