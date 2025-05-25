@file:Suppress("PackageNaming")

package com.mobilispect.mobile.android.ui.frequency_commitment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobilispect.android.navigation.NavDestination
import com.mobilispect.mobile.android.ui.routes.RoutesListRoute

object RoutesListNavDestination : NavDestination {
    override val route: String = "routes_list_route"
    override val destination: String = "routes_list_destination"
}

fun NavGraphBuilder.routesGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    composable(
        route = "${RoutesListNavDestination.route}/{agencyID}",
        arguments = listOf(navArgument("agencyID") { type = NavType.StringType })
    ) {
        RoutesListRoute()
    }
    nestedGraphs()
}
