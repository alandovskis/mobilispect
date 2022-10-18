@file:Suppress("FunctionNaming")

package com.mobilispect.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mobilispect.android.navigation.MobilispectNavHost
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun App() {
    MobilispectTheme {
        val navController = rememberNavController()
        MobilispectNavHost(navController)
    }
}

