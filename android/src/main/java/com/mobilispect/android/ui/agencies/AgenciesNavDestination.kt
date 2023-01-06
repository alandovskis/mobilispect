package com.mobilispect.android.ui.agencies

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mobilispect.android.navigation.NavDestination
import com.mobilispect.common.data.agency.AgencyRef

object AgenciesNavDestination : NavDestination {
    override val route: String = "agencies_route"
    override val destination: String = "agencies_destination"
}

fun NavGraphBuilder.agenciesGraph(
    navigateToCommitment: (AgencyRef) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = AgenciesNavDestination.route,
        startDestination = AgenciesNavDestination.destination
    ) {
        composable(route = AgenciesNavDestination.destination) {
            AgenciesRoute(navigateToCommitment = navigateToCommitment)
        }
        nestedGraphs()
    }
}
