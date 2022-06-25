package com.mobilispect.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import com.mobilispect.android.ui.frequency.FrequencyCommitmentCard
import com.mobilispect.android.ui.theme.MobilispectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilispectTheme {
                Scaffold {
                    FrequencyCommitmentCard()
                }
            }
        }
    }
}