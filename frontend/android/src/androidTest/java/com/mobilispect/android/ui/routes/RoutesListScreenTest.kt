package com.mobilispect.android.ui.routes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.mobilispect.android.ui.theme.MobilispectTheme
import org.junit.Rule
import org.junit.Test

class RoutesListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingIfLoading() {
        composeTestRule.setContent {
            MobilispectTheme {
                RouteListScreen(uiState = Loading)
            }
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun showsRoutesIfThereSome() {
        composeTestRule.setContent {
            MobilispectTheme {
                RouteListScreen(
                    uiState = RoutesFound(
                        routes = listOf(
                            RouteUIState(
                                id = "r-abcd-1",
                                shortName = "1",
                                longName = "Main Street",
                                agencyID = "o-abcd-a"
                            ),
                            RouteUIState(
                                id = "r-abcd-2",
                                shortName = "2",
                                longName = "Central Avenue",
                                agencyID = "o-abcd-a"
                            )
                        )
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("1: Main Street").assertIsDisplayed()
        composeTestRule.onNodeWithText("2: Central Avenue").assertIsDisplayed()
    }
}