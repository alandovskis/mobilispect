@file:Suppress("FunctionNaming")

package com.mobilispect.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mobilispect.android.ui.frequency_commitment.FrequencyCommitmentNavDestination
import com.mobilispect.android.ui.frequency_commitment.frequencyGraph
import com.mobilispect.android.ui.frequency_violation.FrequencyViolationDestination
import com.mobilispect.android.ui.frequency_violation.violationGraph

@Composable
fun MobilispectNavHost(
    navController: NavHostController,
    startDestination: String = FrequencyCommitmentNavDestination.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        frequencyGraph(
            navigateToViolation = { navController.navigate("${FrequencyViolationDestination.route}/${it.id}") },
            nestedGraphs = {
                violationGraph()
            }
        )
    }
}
