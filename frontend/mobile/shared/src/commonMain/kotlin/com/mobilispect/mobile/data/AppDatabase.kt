package com.mobilispect.common.data

import android.content.Context
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.mobilispect.mobile.data.agency.Agency
import com.mobilispect.mobile.data.agency.AgencyDAO
import com.mobilispect.mobile.data.route.Route
import com.mobilispect.mobile.data.route.RouteDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.Volatile

@Database(
    version = 4,
    exportSchema = true,
    entities = [
        Agency::class,
        Route::class,
    ]
)
@TypeConverters(RoomTypeConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agencyDAO(): AgencyDAO
    abstract fun routeDAO(): RouteDAO
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
) : AppDatabase {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}