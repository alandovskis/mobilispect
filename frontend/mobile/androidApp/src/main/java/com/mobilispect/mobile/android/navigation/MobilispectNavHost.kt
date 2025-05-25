@file:Suppress("FunctionNaming")

package com.mobilispect.mobile.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mobilispect.android.ui.agencies.AgenciesNavDestination
import com.mobilispect.android.ui.agencies.agenciesGraph
import com.mobilispect.android.ui.frequency_commitment.RoutesListNavDestination
import com.mobilispect.android.ui.frequency_commitment.routesGraph
import com.mobilispect.android.ui.frequency_violation.violationGraph

@Composable
fun MobilispectNavHost(
    navController: NavHostController,
    startDestination: String = AgenciesNavDestination.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        agenciesGraph(
            navigateToRoutes = { navController.navigate("${RoutesListNavDestination.route}/${it}") },
            nestedGraphs = {
                routesGraph(
                    nestedGraphs = {
                        violationGraph()
                    }
                )
            })
    }
}
