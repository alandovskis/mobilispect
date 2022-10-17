package com.mobilispect.common.data.transit_land

import com.google.gson.Gson
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.mobilispect.common.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object TransitLandAPIModule {
    @Provides
    fun api(): TransitLandAPI {
        val okhttp = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okhttp.addInterceptor(OkHttpProfilerInterceptor())
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://transit.land/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(okhttp.build())
            .build()
        return retrofit.create(TransitLandAPI::class.java)
    }
}