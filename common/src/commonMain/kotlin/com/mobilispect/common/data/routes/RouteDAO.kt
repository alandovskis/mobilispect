package com.mobilispect.common.data.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface RouteDAO {
    @Insert
    suspend fun insert(route: Route)

    @Query("SELECT * FROM routes WHERE id = :ref")
    suspend fun withRef(ref: RouteRef): Route?
}
