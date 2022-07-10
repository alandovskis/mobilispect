package com.mobilispect.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.android.ui.frequency.FrequencyViolationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilispectTheme {
                //FrequencyCommitmentCard()
                FrequencyViolationScreen()
            }
        }
    }

}