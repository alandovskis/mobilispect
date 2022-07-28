package com.mobilispect.common.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobilispect.common.data.routes.RoomTypeConverters
import com.mobilispect.data.routes.Route
import com.mobilispect.data.routes.RouteDAO

@Database(version = 1, exportSchema = true, entities = [
    Route::class,
])
@TypeConverters(RoomTypeConverters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDAO(): RouteDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                ).build()
                INSTANCE = instance
                instance
            }
    }
}