package com.mobilispect.android.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobilispect.android.ui.frequency.FrequencyViolationRoute

object FrequencyViolationDestination : NavDestination{
    override val route: String = "frequency_violation_route"
    override val destination: String = "frequency_violation_destination"
}

fun NavGraphBuilder.violationGraph() {
    composable(route = "${FrequencyViolationDestination.route}/{routeRef}",
        arguments = listOf(navArgument("routeRef") { type = NavType.StringType })) { backStackEntry ->
        FrequencyViolationRoute(backStackEntry.arguments?.getString("routeRef"))
    }
}
