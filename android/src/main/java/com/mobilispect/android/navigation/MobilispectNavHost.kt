@file:Suppress("FunctionNaming")

package com.mobilispect.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mobilispect.android.ui.agencies.AgenciesNavDestination
import com.mobilispect.android.ui.agencies.agenciesGraph
import com.mobilispect.android.ui.frequency_commitment.FrequencyCommitmentNavDestination
import com.mobilispect.android.ui.frequency_commitment.frequencyGraph
import com.mobilispect.android.ui.frequency_violation.FrequencyViolationDestination
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
            navigateToCommitment = { navController.navigate("${FrequencyCommitmentNavDestination.route}/${it.id}") },
            nestedGraphs = {
                frequencyGraph(
                    navigateToViolation = { navController.navigate("${FrequencyViolationDestination.route}/${it.id}") },
                    nestedGraphs = {
                        violationGraph()
                    }
                )
            })
    }
}
