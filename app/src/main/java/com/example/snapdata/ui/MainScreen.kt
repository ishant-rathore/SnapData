package com.example.snapdata.ui

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.snapdata.ui.screens.*
import com.example.snapdata.ui.screens.auth.*
import com.example.snapdata.ui.screens.guide.UserGuideScreen
import com.example.snapdata.ui.screens.landing.LandingScreen
import com.example.snapdata.ui.theme.*

@Composable
fun MainScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Full-screen Pre-Auth, Guide & Landing experiences without BottomBar/NavRail
    when (uiState.currentScreen) {
        AppScreen.SPLASH -> {
            SnapDataSplashScreen(
                onSplashFinished = {
                    viewModel.navigateTo(AppScreen.LANDING)
                }
            )
            return
        }
        AppScreen.LANDING -> {
            LandingScreen(viewModel)
            return
        }
        AppScreen.USER_GUIDE -> {
            UserGuideScreen(viewModel)
            return
        }
        AppScreen.AUTH_WELCOME -> {
            AuthWelcomeScreen(
                isFirebaseConfigured = viewModel.isFirebaseConfigured,
                onSignInClick = { viewModel.navigateTo(AppScreen.SIGN_IN) },
                onCreateAccountClick = { viewModel.navigateTo(AppScreen.SIGN_UP) },
                onGuestClick = { viewModel.continueAsGuest() },
                onBackToLanding = { viewModel.navigateTo(AppScreen.LANDING) }
            )
            return
        }
        AppScreen.SIGN_IN -> {
            SignInScreen(
                isFirebaseConfigured = viewModel.isFirebaseConfigured,
                onSignIn = { email, pass -> viewModel.signIn(email, pass) },
                onSkipAsGuest = { viewModel.continueAsGuest() },
                onForgotPasswordClick = { viewModel.navigateTo(AppScreen.FORGOT_PASSWORD) },
                onCreateAccountClick = { viewModel.navigateTo(AppScreen.SIGN_UP) },
                onBackClick = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) },
                isLoading = uiState.isAuthLoading,
                authError = uiState.authError,
                onClearError = { viewModel.clearAuthError() }
            )
            return
        }
        AppScreen.SIGN_UP -> {
            SignUpScreen(
                onSignUp = { name, email, pass -> viewModel.signUp(name, email, pass) },
                onSignInClick = { viewModel.navigateTo(AppScreen.SIGN_IN) },
                onBackClick = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) },
                isLoading = uiState.isAuthLoading,
                authError = uiState.authError,
                onClearError = { viewModel.clearAuthError() }
            )
            return
        }
        AppScreen.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onSendResetLink = { email -> viewModel.sendPasswordReset(email) },
                onBackToSignIn = { viewModel.navigateTo(AppScreen.SIGN_IN) },
                isLoading = uiState.isAuthLoading,
                authError = uiState.authError,
                isResetSent = uiState.isResetSent,
                onClearError = { viewModel.clearAuthError() }
            )
            return
        }
        AppScreen.VERIFY_EMAIL -> {
            EmailVerificationScreen(
                userEmail = viewModel.currentUser?.email.orEmpty(),
                onCheckVerified = { viewModel.checkEmailVerified() },
                onResendEmail = { viewModel.sendEmailVerification() },
                onSignOutOrBack = { viewModel.signOut() },
                onProceedAnyway = { viewModel.navigateTo(AppScreen.HOME) },
                isLoading = uiState.isAuthLoading,
                authError = uiState.authError,
                isResendSuccess = uiState.isResendSuccess
            )
            return
        }
        AppScreen.AUTH_SUCCESS -> {
            AuthSuccessScreen(
                onTransitionComplete = { viewModel.navigateTo(AppScreen.HOME) }
            )
            return
        }
        else -> {
            // Protected Application screens
        }
    }

    val isTopLevelScreen = uiState.currentScreen in listOf(AppScreen.HOME, AppScreen.HISTORY, AppScreen.SETTINGS)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen && isTopLevelScreen) {
            // Adaptive Tablet / Landscape Dual-Pane Layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = CardWhite,
                    modifier = Modifier.testTag("tablet_navigation_rail"),
                    header = {
                        FloatingActionButton(
                            onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                            containerColor = SnapDataRed,
                            contentColor = CardWhite,
                            elevation = FloatingActionButtonDefaults.elevation(2.dp),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .testTag("nav_rail_scan_fab")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan Document")
                        }
                    }
                ) {
                    NavigationRailItem(
                        selected = uiState.currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(if (uiState.currentScreen == AppScreen.HOME) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = SnapDataRed,
                            selectedTextColor = SnapDataRed,
                            indicatorColor = SnapDataRedLight
                        ),
                        modifier = Modifier.testTag("nav_rail_item_home")
                    )
                    NavigationRailItem(
                        selected = uiState.currentScreen == AppScreen.HISTORY,
                        onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                        icon = { Icon(if (uiState.currentScreen == AppScreen.HISTORY) Icons.Default.Folder else Icons.Outlined.Folder, contentDescription = "Archive") },
                        label = { Text("Archive") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = SnapDataRed,
                            selectedTextColor = SnapDataRed,
                            indicatorColor = SnapDataRedLight
                        ),
                        modifier = Modifier.testTag("nav_rail_item_history")
                    )
                    NavigationRailItem(
                        selected = uiState.currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = { Icon(if (uiState.currentScreen == AppScreen.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = SnapDataRed,
                            selectedTextColor = SnapDataRed,
                            indicatorColor = SnapDataRedLight
                        ),
                        modifier = Modifier.testTag("nav_rail_item_settings")
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ScreenContent(uiState.currentScreen, viewModel)
                }
            }
        } else {
            // Standard Mobile Scaffold with Clean Bottom NavigationBar
            Scaffold(
                containerColor = WarmCreamBackground,
                bottomBar = {
                    if (isTopLevelScreen) {
                        Surface(
                            color = CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LightBorder),
                            shadowElevation = 8.dp
                        ) {
                            NavigationBar(
                                containerColor = CardWhite,
                                tonalElevation = 0.dp,
                                modifier = Modifier.testTag("main_navigation_bar")
                            ) {
                                NavigationBarItem(
                                    selected = uiState.currentScreen == AppScreen.HOME,
                                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                                    icon = { Icon(if (uiState.currentScreen == AppScreen.HOME) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SnapDataRed,
                                        selectedTextColor = SnapDataRed,
                                        indicatorColor = SnapDataRedLight
                                    ),
                                    modifier = Modifier.testTag("nav_item_home")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentScreen == AppScreen.ACQUISITION,
                                    onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                                    icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = "Scan Document") },
                                    label = { Text("Scan") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SnapDataRed,
                                        selectedTextColor = SnapDataRed,
                                        indicatorColor = SnapDataRedLight
                                    ),
                                    modifier = Modifier.testTag("nav_item_scan")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentScreen == AppScreen.HISTORY,
                                    onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                                    icon = { Icon(if (uiState.currentScreen == AppScreen.HISTORY) Icons.Default.Folder else Icons.Outlined.Folder, contentDescription = "Archive") },
                                    label = { Text("Archive") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SnapDataRed,
                                        selectedTextColor = SnapDataRed,
                                        indicatorColor = SnapDataRedLight
                                    ),
                                    modifier = Modifier.testTag("nav_item_history")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentScreen == AppScreen.SETTINGS,
                                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                                    icon = { Icon(if (uiState.currentScreen == AppScreen.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = SnapDataRed,
                                        selectedTextColor = SnapDataRed,
                                        indicatorColor = SnapDataRedLight
                                    ),
                                    modifier = Modifier.testTag("nav_item_settings")
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    ScreenContent(uiState.currentScreen, viewModel)
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(screen: AppScreen, viewModel: SnapDataViewModel) {
    when (screen) {
        AppScreen.SPLASH -> SnapDataSplashScreen(
            onSplashFinished = {
                viewModel.navigateTo(AppScreen.LANDING)
            }
        )
        AppScreen.HOME -> HomeScreen(viewModel)
        AppScreen.ACQUISITION -> AcquisitionScreen(viewModel)
        AppScreen.PREPROCESSING -> PreprocessingScreen(viewModel)
        AppScreen.PROCESSING -> ProcessingScreen(viewModel)
        AppScreen.REVIEW_EDITOR -> ReviewEditorScreen(viewModel)
        AppScreen.EXPORT -> ExportScreen(viewModel)
        AppScreen.HISTORY -> HistoryScreen(viewModel)
        AppScreen.SETTINGS -> SettingsScreen(viewModel)
        AppScreen.LANDING -> LandingScreen(viewModel)
        AppScreen.USER_GUIDE -> UserGuideScreen(viewModel)
        AppScreen.AUTH_WELCOME -> AuthWelcomeScreen(
            onSignInClick = { viewModel.navigateTo(AppScreen.SIGN_IN) },
            onCreateAccountClick = { viewModel.navigateTo(AppScreen.SIGN_UP) },
            onGuestClick = { viewModel.continueAsGuest() },
            onBackToLanding = { viewModel.navigateTo(AppScreen.LANDING) }
        )
        AppScreen.SIGN_IN -> SignInScreen(
            onSignIn = { email, pass -> viewModel.signIn(email, pass) },
            onForgotPasswordClick = { viewModel.navigateTo(AppScreen.FORGOT_PASSWORD) },
            onCreateAccountClick = { viewModel.navigateTo(AppScreen.SIGN_UP) },
            onBackClick = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) }
        )
        AppScreen.SIGN_UP -> SignUpScreen(
            onSignUp = { name, email, pass -> viewModel.signUp(name, email, pass) },
            onSignInClick = { viewModel.navigateTo(AppScreen.SIGN_IN) },
            onBackClick = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) }
        )
        AppScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(
            onSendResetLink = { email -> viewModel.sendPasswordReset(email) },
            onBackToSignIn = { viewModel.navigateTo(AppScreen.SIGN_IN) }
        )
        AppScreen.VERIFY_EMAIL -> EmailVerificationScreen(
            userEmail = viewModel.currentUser?.email.orEmpty(),
            onCheckVerified = { viewModel.checkEmailVerified() },
            onResendEmail = { viewModel.sendEmailVerification() },
            onSignOutOrBack = { viewModel.signOut() },
            onProceedAnyway = { viewModel.navigateTo(AppScreen.HOME) }
        )
        AppScreen.AUTH_SUCCESS -> AuthSuccessScreen(
            onTransitionComplete = { viewModel.navigateTo(AppScreen.HOME) }
        )
    }
}
