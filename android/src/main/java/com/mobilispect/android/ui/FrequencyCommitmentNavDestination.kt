package com.mobilispect.android.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mobilispect.android.ui.frequency.FrequencyCommitmentRoute
import com.mobilispect.data.routes.RouteRef

object FrequencyCommitmentNavDestination : NavDestination {
    override val route: String = "frequency_commitment_route"
    override val destination: String = "frequency_commitment_destination"
}

fun NavGraphBuilder.frequencyGraph(
    navigateToViolation: (RouteRef) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = FrequencyCommitmentNavDestination.route,
        startDestination = FrequencyCommitmentNavDestination.destination
    ) {
        composable(route = FrequencyCommitmentNavDestination.destination) {
            FrequencyCommitmentRoute(
                navigateToViolation = navigateToViolation
            )
        }
        nestedGraphs()
    }
}