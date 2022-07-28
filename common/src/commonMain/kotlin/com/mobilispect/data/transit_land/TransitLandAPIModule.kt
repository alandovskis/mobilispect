package com.mobilispect.data.transit_land

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object TransitLandAPIModule {
    @Provides
    fun api(): TransitLandAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://transit.land/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
        return retrofit.create(TransitLandAPI::class.java)
    }
}