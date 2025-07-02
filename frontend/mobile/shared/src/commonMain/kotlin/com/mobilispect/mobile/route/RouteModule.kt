package com.mobilispect.mobile.route

import com.mobilispect.common.data.route.RouteNetworkDataSource
import com.mobilispect.mobile.data.AppDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val routeModule = module {
    single<RouteDAO> {
        val database = get<AppDatabase>()
        database.routeDAO()
    }

}