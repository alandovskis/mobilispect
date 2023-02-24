package com.mobilispect.android.ui.agencies

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mobilispect.android.navigation.NavDestination

object AgenciesNavDestination : NavDestination {
    override val route: String = "agencies_route"
    override val destination: String = "agencies_destination"
}

fun NavGraphBuilder.agenciesGraph(
    navigateToRoutes: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = AgenciesNavDestination.route,
        startDestination = AgenciesNavDestination.destination
    ) {
        composable(route = AgenciesNavDestination.destination) {
            AgenciesRoute(navigateToCommitment = navigateToRoutes)
        }
        nestedGraphs()
    }
}
