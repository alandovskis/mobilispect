package com.mobilispect.mobile.route

import com.mobilispect.common.data.route.RouteNetworkDataSource
import com.mobilispect.mobile.data.AppDatabase
import com.mobilispect.mobile.data.transit_land.TransitLandAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val routeModule = module {
    single<RouteDAO> {
        val database = get<AppDatabase>()
        database.routeDAO()
    }

    singleOf(::TransitLandRouteDataSource) bind RouteNetworkDataSource::class

}