package com.mobilispect.android.data.routes

import com.google.gson.Gson
import com.mobilispect.common.data.routes.TransitLandAPI
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