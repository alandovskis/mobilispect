package com.mobilispect.common.data.routes

import androidx.room.TypeConverter
import com.mobilispect.data.routes.RouteRef

class RoomTypeConverters {
   companion object {
       @TypeConverter
       @JvmStatic
       fun routeRefFromDB(ref: String): RouteRef? {
           val components = ref.split("-")
           if (components.size < 3) {
               return null
           }
           return RouteRef(
               geohash = components[1],
               routeNumber = components[2]
           )
       }

       @TypeConverter
       @JvmStatic
       fun routeRefToDB(ref: RouteRef): String = ref.id
   }
}