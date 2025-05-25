package com.mobilispect.common.data.agency

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AgencyDAO {
    @Insert
    suspend fun insert(agency: Agency)

    @Delete
    suspend fun delete(agency: Agency)

    @Query("SELECT * FROM agencies")
    fun all(): Flow<List<Agency>>
}