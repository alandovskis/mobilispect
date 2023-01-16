package com.mobilispect.common.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobilispect.common.data.agency.Agency
import com.mobilispect.common.data.agency.AgencyDAO
import com.mobilispect.common.data.route.Route
import com.mobilispect.common.data.route.RouteDAO

@Database(
    version = 2,
    exportSchema = true,
    entities = [
        Agency::class,
        Route::class,
    ]
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agencyDAO(): AgencyDAO
    abstract fun routeDAO(): RouteDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "app.db"
            ).fallbackToDestructiveMigration().build()
            INSTANCE = instance
            instance
        }
    }
}