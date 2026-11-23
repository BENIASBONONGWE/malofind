package com.example.malofind

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.malofind.ui.theme.MaloFindTheme
import com.example.malofind.ui.theme.screens.auth.AdminLoginScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AdminLoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adminLoginScreen_displaysTitleAndFields() {
        composeTestRule.setContent {
            MaloFindTheme {
                AdminLoginScreen(
                    onLoginSuccess = {},
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Administrator Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in to manage MaloFind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Administrator email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In as Administrator").assertIsDisplayed()
    }

    @Test
    fun adminLoginScreen_clickingSignInWithEmptyFields_showsError() {
        composeTestRule.setContent {
            MaloFindTheme {
                AdminLoginScreen(
                    onLoginSuccess = {},
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Sign In as Administrator").performClick()
        composeTestRule.onNodeWithText("Enter your email and password.").assertIsDisplayed()
    }

    @Test
    fun adminLoginScreen_clickingBack_triggersCallback() {
        var backClicked = false

        composeTestRule.setContent {
            MaloFindTheme {
                AdminLoginScreen(
                    onLoginSuccess = {},
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Back").performClick()
        assertTrue(backClicked)
    }
}
