package com.mobilispect.android.ui.frequency_commitment

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mobilispect.android.ui.theme.MobilispectTheme
import com.mobilispect.common.data.route.RouteRef
import com.mobilispect.common.data.time.WEEKDAYS
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

class FrequencyCommitmentScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bidirectionalFrequency() {
        composeTestRule.setContent {
            MobilispectTheme {
                FrequencyCommitmentScreen(
                    uiState = FrequencyCommitmentUIState(
                        items = listOf(
                            FrequencyCommitmentItemUIState(
                                directions = listOf(
                                    FrequencyCommitmentDirectionUIState(
                                        direction = null,
                                        startTime = LocalTime.of(6, 0),
                                        endTime = LocalTime.of(21, 0)
                                    )
                                ),
                                daysOfTheWeek = WEEKDAYS,
                                frequency = FrequencyCommitmentFrequencyUIState(frequency = 10),
                                routes = listOf(
                                    RouteUIState(
                                        route = "141",
                                        routeRef = RouteRef(geohash = "a1d2", "141")
                                    )
                                )
                            )
                        )
                    ),
                    navigateToViolation = {}
                )
            }
        }

        composeTestRule.onNodeWithText("ON Weekdays")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("FROM 6:00 AM TO 9:00 PM")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("EVERY 10 MINUTES OR LESS")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("141")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun directionalFrequency() {
        composeTestRule.setContent {
            MobilispectTheme {
                FrequencyCommitmentScreen(
                    uiState = FrequencyCommitmentUIState(
                        items = listOf(
                            FrequencyCommitmentItemUIState(
                                directions = listOf(
                                    FrequencyCommitmentDirectionUIState(
                                        direction = com.mobilispect.data.frequency.Direction.Inbound,
                                        startTime = LocalTime.of(6, 0),
                                        endTime = LocalTime.of(14, 0)
                                    ),
                                    FrequencyCommitmentDirectionUIState(
                                        direction = com.mobilispect.data.frequency.Direction.Outbound,
                                        startTime = LocalTime.of(14, 0),
                                        endTime = LocalTime.of(21, 0)
                                    )
                                ),
                                daysOfTheWeek = WEEKDAYS,
                                frequency = FrequencyCommitmentFrequencyUIState(frequency = 10),
                                routes = listOf(
                                    RouteUIState(
                                        route = "141",
                                        routeRef = RouteRef(geohash = "a1d2", "141")
                                    )
                                )
                            )
                        )
                    ),
                    navigateToViolation = {}
                )
            }
        }

        SystemClock.sleep(5000)

        composeTestRule.onNodeWithText("ON Weekdays")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("INBOUND FROM 6:00 AM TO 2:00 PM")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("OUTBOUND FROM 2:00 PM TO 9:00 PM")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("EVERY 10 MINUTES OR LESS")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("141")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
