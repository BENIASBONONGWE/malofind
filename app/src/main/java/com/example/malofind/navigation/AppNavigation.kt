package com.example.malofind.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.malofind.data.repository.AdminRepository
import com.example.malofind.data.repository.AuthRepository

import com.example.malofind.ui.theme.screens.admin.AdminDashboardScreen
import com.example.malofind.ui.theme.screens.admin.ManageOwnersScreen
import com.example.malofind.ui.theme.screens.admin.ManagePropertiesScreen
import com.example.malofind.ui.theme.screens.admin.ManageUsersScreen
import com.example.malofind.ui.theme.screens.admin.PropertyApprovalScreen

import com.example.malofind.ui.theme.screens.auth.AdminLoginScreen
import com.example.malofind.ui.theme.screens.auth.ForgotPasswordScreen
import com.example.malofind.ui.theme.screens.auth.LoginScreen
import com.example.malofind.ui.theme.screens.auth.RegisterScreen
import com.example.malofind.ui.theme.screens.auth.RoleSelectionScreen

import com.example.malofind.ui.theme.screens.common.NotificationsScreen
import com.example.malofind.ui.theme.screens.common.SettingsScreen

import com.example.malofind.ui.theme.screens.owner.AddPropertyScreen
import com.example.malofind.ui.theme.screens.owner.ApprovalStatusScreen
import com.example.malofind.ui.theme.screens.owner.EditPropertyScreen
import com.example.malofind.ui.theme.screens.owner.MyPropertiesScreen
import com.example.malofind.ui.theme.screens.owner.OwnerDashboardScreen
import com.example.malofind.ui.theme.screens.owner.OwnerRequestsScreen

import com.example.malofind.ui.theme.screens.seeker.FavouritesScreen
import com.example.malofind.ui.theme.screens.seeker.MyRequestsScreen
import com.example.malofind.ui.theme.screens.seeker.PropertyDetailsScreen
import com.example.malofind.ui.theme.screens.seeker.PropertyMapScreen
import com.example.malofind.ui.theme.screens.seeker.SearchScreen
import com.example.malofind.ui.theme.screens.seeker.SeekerDashboardScreen
import com.example.malofind.ui.theme.screens.seeker.SeekerProfileScreen
import com.example.malofind.ui.theme.screens.seeker.SubmitRequestScreen

import com.example.malofind.ui.theme.screens.splash.SplashScreen
import com.example.malofind.ui.theme.screens.welcome.WelcomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { accountType ->
                    when (accountType.uppercase()) {
                        "ADMIN" -> {
                            navController.navigate(Routes.ADMIN_DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                        "OWNER" -> {
                            navController.navigate(Routes.OWNER_DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                        "SEEKER" -> {
                            navController.navigate(Routes.SEEKER_DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Routes.ROLE_SELECTION) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    }
                },
                onRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { accountType ->
                    when (accountType.uppercase()) {
                        "ADMIN" -> {
                            navController.navigate(Routes.ADMIN_DASHBOARD) {
                                popUpTo(Routes.WELCOME) { inclusive = false }
                            }
                        }
                        "OWNER" -> {
                            navController.navigate(Routes.OWNER_DASHBOARD) {
                                popUpTo(Routes.WELCOME) { inclusive = false }
                            }
                        }
                        else -> {
                            navController.navigate(Routes.SEEKER_DASHBOARD) {
                                popUpTo(Routes.WELCOME) { inclusive = false }
                            }
                        }
                    }
                },
                onLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onContinue = { role ->
                    when (role.uppercase()) {
                        "ADMIN" -> {
                            navController.navigate(Routes.ADMIN_LOGIN)
                        }
                        "OWNER" -> {
                            navController.navigate(Routes.OWNER_DASHBOARD) {
                                popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Routes.SEEKER_DASHBOARD) {
                                popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.ADMIN_LOGIN) {
            AdminLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.ADMIN_DASHBOARD) {
                        popUpTo(Routes.ADMIN_LOGIN) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SEEKER_DASHBOARD) {
            SeekerDashboardScreen(
                onPropertyClick = { propertyId ->
                    navController.navigate("${Routes.PROPERTY_DETAILS}/$propertyId")
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onProfileClick = {
                    navController.navigate(Routes.SEEKER_PROFILE)
                }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPropertyClick = { propertyId ->
                    navController.navigate("${Routes.PROPERTY_DETAILS}/$propertyId")
                }
            )
        }

        composable(
            route = "${Routes.PROPERTY_DETAILS}/{propertyId}",
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailsScreen(
                propertyId = propertyId,
                onBack = {
                    navController.popBackStack()
                },
                onMapClick = {
                    navController.navigate("${Routes.PROPERTY_MAP}/$propertyId")
                },
                onRequestProperty = {
                    navController.navigate("${Routes.SUBMIT_REQUEST}/$propertyId")
                },
                onCallOwner = {}
            )
        }

        composable(
            route = "${Routes.PROPERTY_MAP}/{propertyId}",
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            PropertyMapScreen(
                propertyId = backStackEntry.arguments?.getString("propertyId") ?: "",
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Routes.SUBMIT_REQUEST}/{propertyId}",
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            SubmitRequestScreen(
                propertyId = backStackEntry.arguments?.getString("propertyId") ?: "",
                onBack = {
                    navController.popBackStack()
                },
                onRequestSubmitted = {
                    navController.navigate(Routes.MY_REQUESTS)
                }
            )
        }

        composable(Routes.FAVOURITES) {
            FavouritesScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPropertyClick = { propertyId ->
                    navController.navigate("${Routes.PROPERTY_DETAILS}/$propertyId")
                }
            )
        }

        composable(Routes.MY_REQUESTS) {
            MyRequestsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SEEKER_PROFILE) {
            SeekerProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRequestsClick = {
                    navController.navigate(Routes.MY_REQUESTS)
                },
                onFavouritesClick = {
                    navController.navigate(Routes.FAVOURITES)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.OWNER_DASHBOARD) {
            OwnerDashboardScreen(
                onAddPropertyClick = {
                    navController.navigate(Routes.ADD_PROPERTY)
                },
                onMyPropertiesClick = {
                    navController.navigate(Routes.MY_PROPERTIES)
                },
                onApprovalStatusClick = {
                    navController.navigate(Routes.APPROVAL_STATUS)
                },
                onRequestsClick = {
                    navController.navigate(Routes.OWNER_REQUESTS)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onProfileClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLogoutClick = {
                    AuthRepository().logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADD_PROPERTY) {
            AddPropertyScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPropertySubmitted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MY_PROPERTIES) {
            MyPropertiesScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddPropertyClick = {
                    navController.navigate(Routes.ADD_PROPERTY)
                },
                onEditPropertyClick = { propertyId ->
                    navController.navigate("${Routes.EDIT_PROPERTY}/$propertyId")
                },
                onViewPropertyClick = { propertyId ->
                    navController.navigate("${Routes.PROPERTY_DETAILS}/$propertyId")
                }
            )
        }

        composable(
            route = "${Routes.EDIT_PROPERTY}/{propertyId}",
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            EditPropertyScreen(
                propertyId = propertyId,
                onBack = {
                    navController.popBackStack()
                },
                onPropertyUpdated = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.APPROVAL_STATUS) {
            ApprovalStatusScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRefresh = {}
            )
        }

        composable(Routes.OWNER_REQUESTS) {
            OwnerRequestsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRefresh = {}
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onManageUsersClick = {
                    navController.navigate(Routes.MANAGE_USERS)
                },
                onManageOwnersClick = {
                    navController.navigate(Routes.MANAGE_OWNERS)
                },
                onManagePropertiesClick = {
                    navController.navigate(Routes.MANAGE_PROPERTIES)
                },
                onPropertyApprovalClick = {
                    navController.navigate(Routes.PROPERTY_APPROVAL)
                },
                onReportsClick = {
                    navController.navigate(Routes.REPORTS)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.NOTIFICATIONS)
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLogoutClick = {
                    AdminRepository().logoutAdmin()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MANAGE_USERS) {
            ManageUsersScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MANAGE_OWNERS) {
            ManageOwnersScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MANAGE_PROPERTIES) {
            ManagePropertiesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PROPERTY_APPROVAL) {
            PropertyApprovalScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    AuthRepository().logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
