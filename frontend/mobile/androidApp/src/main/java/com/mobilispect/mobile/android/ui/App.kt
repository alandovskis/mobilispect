package com.mobilispect.mobile.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mobilispect.mobile.android.navigation.MobilispectNavHost
import com.mobilispect.android.ui.theme.MobilispectTheme

@Composable
fun App() {
    MobilispectTheme {
        val navController = rememberNavController()
        MobilispectNavHost(navController)
    }
}

