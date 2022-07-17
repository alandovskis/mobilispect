package com.mobilispect.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mobilispect.android.ui.FrequencyCommitmentNavDestination
import com.mobilispect.android.ui.FrequencyViolationDestination
import com.mobilispect.android.ui.frequencyGraph
import com.mobilispect.android.ui.violationGraph

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