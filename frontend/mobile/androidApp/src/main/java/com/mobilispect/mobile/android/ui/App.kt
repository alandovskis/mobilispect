package com.mobilispect.mobile.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.mobile.android.navigation.MobilispectNavHost

@Composable
fun App() {
    MobilispectTheme {
        val navController = rememberNavController()
        MobilispectNavHost(navController)
    }
}

@Composable
@Preview
fun AppPreview() {
    MobilispectTheme {
        App()
    }
}

