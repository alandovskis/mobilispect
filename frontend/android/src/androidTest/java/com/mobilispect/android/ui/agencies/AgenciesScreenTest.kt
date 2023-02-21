package com.mobilispect.android.ui.agencies

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.mobilispect.android.ui.theme.MobilispectTheme
import org.junit.Rule
import org.junit.Test

private val AGENCY_A = AgencyUIState(
    id = "o-abcd-A", name = "A"
)
private val AGENCY_B = AgencyUIState(
    id = "o-abcd-B", name = "B"
)

class AgenciesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsNothingWhenNoAgenciesFound() {
        composeTestRule.setContent {
            MobilispectTheme {
                AgenciesScreen(uiState = NoAgencyFound, navigateToCommitment = {})
            }
        }

        composeTestRule.onNodeWithText("No agency was found.").assertIsDisplayed()
    }

    @Test
    fun showsAgenciesWhenThereAreSome() {
        composeTestRule.setContent {
            MobilispectTheme {
                AgenciesScreen(uiState = AgenciesFound(
                    agencies = listOf(
                        AGENCY_A, AGENCY_B
                    )
                ), navigateToCommitment = {})
            }
        }

        composeTestRule.onNodeWithText(AGENCY_A.name).assertIsDisplayed()

        composeTestRule.onNodeWithText(AGENCY_B.name).assertIsDisplayed()
    }
}