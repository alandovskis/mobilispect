package com.mobilispect.android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mobilispect.android.ui.FrequencyCommitmentNavDestination
import com.mobilispect.android.ui.FrequencyViolationDestination
import com.mobilispect.android.ui.frequencyGraph
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.android.ui.violationGraph

@Composable
fun App() {
    MobilispectTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = FrequencyCommitmentNavDestination.route,
        ) {
            frequencyGraph(
                navigateToViolation = { navController.navigate("${FrequencyViolationDestination.route}/${it.id}") },
                nestedGraphs = {
                    violationGraph()
                }
            )
        }
    }
}
