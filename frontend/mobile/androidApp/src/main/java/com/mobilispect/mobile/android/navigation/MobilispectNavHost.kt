@file:Suppress("FunctionNaming")

package com.mobilispect.mobile.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mobilispect.mobile.android.ui.agencies.AgenciesList
import com.mobilispect.mobile.android.ui.agencies.agenciesGraph

@Composable
fun MobilispectNavHost(
    navController: NavHostController,
    startDestination: AgenciesList = AgenciesList
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        agenciesGraph()
    }
}
