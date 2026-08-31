package com.example.snapdata.ui.screens.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.screens.landing.components.*
import com.example.snapdata.ui.theme.WarmOffWhite
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(viewModel: SnapDataViewModel) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Short splash animation duration
        showSplash = false
    }

    if (showSplash) {
        SplashSection()
    } else {
        LandingContent(viewModel)
    }
}

@Composable
fun LandingContent(viewModel: SnapDataViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
    ) {
        HeroSection(
            onGetStarted = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) },
            onSeeHowItWorks = { /* Scroll to workflow */ }
        )
        WorkflowSection()
        FeatureShowcaseSection()
        PrivacySection()
        DemoAndCTASection(
            onGetStarted = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) },
            onSignIn = { viewModel.navigateTo(AppScreen.SIGN_IN) }
        )
    }
}
