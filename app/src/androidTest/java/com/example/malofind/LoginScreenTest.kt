package com.example.malofind

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.malofind.ui.theme.MaloFindTheme
import com.example.malofind.ui.theme.screens.auth.LoginScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysTitleAndButtons() {
        composeTestRule.setContent {
            MaloFindTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onRegister = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in to continue using MaloFind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create an account").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickingSignInWithEmptyFields_showsError() {
        composeTestRule.setContent {
            MaloFindTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onRegister = {},
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Sign In").performClick()
        composeTestRule.onNodeWithText("Please enter your email and password.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickingCreateAccount_triggersCallback() {
        var registerClicked = false

        composeTestRule.setContent {
            MaloFindTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onRegister = { registerClicked = true },
                    onForgotPassword = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Create an account").performClick()
        assertTrue(registerClicked)
    }
}
