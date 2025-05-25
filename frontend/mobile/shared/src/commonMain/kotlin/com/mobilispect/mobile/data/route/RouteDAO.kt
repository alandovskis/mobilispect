package com.mobilispect.common.data.route

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface RouteDAO {
    @Insert
    suspend fun insert(route: Route)

    @Delete
    suspend fun delete(route: Route)

    @Query("SELECT * FROM routes")
    fun all(): Flow<List<Route>>

    @Query("SELECT * FROM routes where agency_id = :agencyID")
    fun operatedBy(agencyID: String): Flow<List<Route>>
}
