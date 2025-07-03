@file:Suppress("PackageNaming")

package com.mobilispect.mobile.android.ui.frequency_commitment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobilispect.mobile.android.ui.routes.RoutesListRoute
import kotlinx.serialization.Serializable

@Serializable
class RoutesList(val agencyID: String)

fun NavGraphBuilder.routesGraph() {
    composable<RoutesList> {
        RoutesListRoute()
    }
}
