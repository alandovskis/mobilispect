package com.mobilispect.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobilispect.android.ui.frequency.FrequencyCommitmentRoute
import com.mobilispect.android.ui.frequency.FrequencyViolationRoute
import com.mobilispect.android.ui.theme.MobilispectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilispectTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = FrequencyCommitmentNavDestination.route,
                ) {
                    frequencyGraph(
                        navigateToViolation = { navController.navigate("${FrequencyViolationDestination.route}/${it.id}")},
                        nestedGraphs = {
                            violationGraph()
                        }
                    )
                }
            }
        }
    }

}