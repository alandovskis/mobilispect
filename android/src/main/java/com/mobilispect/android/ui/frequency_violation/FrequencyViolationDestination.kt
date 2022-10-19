@file:Suppress("PackageNaming")

package com.mobilispect.android.ui.frequency_violation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobilispect.android.navigation.NavDestination

object FrequencyViolationDestination : NavDestination {
    override val route: String = "frequency_violation_route"
    override val destination: String = "frequency_violation_destination"
}

fun NavGraphBuilder.violationGraph() {
    composable(route = "${FrequencyViolationDestination.route}/{routeRef}",
        arguments = listOf(navArgument("routeRef") { type = NavType.StringType })
    ) { backStackEntry ->
        FrequencyViolationRoute(backStackEntry.arguments?.getString("routeRef"))
    }
}
