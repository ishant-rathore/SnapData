package com.example.snapdata.ui.screens.landing

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.snapdata.sample.SampleDocumentRepository
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.screens.landing.components.*
import com.example.snapdata.ui.theme.*
import kotlinx.coroutines.launch

/**
 * SnapData Native Landing Screen
 * Implementation of the minimalist editorial line-art landing page design
 * featuring 11 interactive, responsive, vertically scrollable sections.
 */
@Composable
fun LandingScreen(
    viewModel: SnapDataViewModel,
    modifier: Modifier = Modifier
) {
    SnapDataLandingScreen(
        viewModel = viewModel,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapDataLandingScreen(
    viewModel: SnapDataViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Offsets for section smooth scrolling
    val sectionOffsets = remember { mutableStateMapOf<String, Int>() }

    fun scrollToSection(sectionKey: String) {
        val targetY = sectionOffsets[sectionKey] ?: 0
        coroutineScope.launch {
            scrollState.animateScrollTo(
                value = targetY,
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            )
        }
    }

    fun launchApp() {
        if (viewModel.isLoggedIn) {
            viewModel.navigateTo(AppScreen.HOME)
        } else if (viewModel.isFirebaseConfigured) {
            viewModel.navigateTo(AppScreen.AUTH_WELCOME)
        } else {
            viewModel.continueAsGuest()
        }
    }

    fun onShowcaseScreenSelect(screenTitle: String) {
        when {
            screenTitle.contains("Home", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                viewModel.navigateTo(AppScreen.HOME)
            }
            screenTitle.contains("Camera", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                viewModel.navigateTo(AppScreen.ACQUISITION)
            }
            screenTitle.contains("Processing", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                val sample = SampleDocumentRepository.samples.firstOrNull()
                if (sample != null) {
                    viewModel.selectSampleDocument(sample)
                }
                viewModel.navigateTo(AppScreen.PROCESSING)
            }
            screenTitle.contains("Extracted", ignoreCase = true) || screenTitle.contains("Edit", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                val sample = SampleDocumentRepository.samples.firstOrNull()
                if (sample != null) {
                    viewModel.selectSampleDocument(sample)
                }
                viewModel.navigateTo(AppScreen.REVIEW_EDITOR)
            }
            screenTitle.contains("History", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                viewModel.navigateTo(AppScreen.HISTORY)
            }
            screenTitle.contains("Export", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                val sample = SampleDocumentRepository.samples.firstOrNull()
                if (sample != null) {
                    viewModel.selectSampleDocument(sample)
                }
                viewModel.navigateTo(AppScreen.EXPORT)
            }
            screenTitle.contains("Settings", ignoreCase = true) -> {
                viewModel.continueAsGuest()
                viewModel.navigateTo(AppScreen.SETTINGS)
            }
            else -> {
                launchApp()
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("snapdata_landing_screen"),
        containerColor = WarmCreamBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Navigation Header
            LandingHeader(
                onNavigateSection = { section -> scrollToSection(section) },
                onLaunchApp = { launchApp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["hero"] = coordinates.positionInParent().y.toInt()
                    }
            )

            // 2. Hero Section (with Phone Mockup of Home Dashboard + 4 Floating Cards)
            HeroSection(
                onDownloadClick = { launchApp() },
                onSeeHowItWorksClick = { scrollToSection("workflow") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Document -> Data Workflow Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["workflow"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                WorkflowSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Feature Capabilities Section (01-06 cards)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["features"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                FeatureGridSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. App Showcase Section (8 Real Application Screens)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["showcase"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                AppShowcaseSection(
                    onSelectScreen = { screenTitle -> onShowcaseScreenSelect(screenTitle) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. AI Document Intelligence Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["ai"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                AiIntelligenceSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Table Extraction Section (Interactive Live Table)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["tables"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                TableExtractionSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 8. On-Device Privacy & Security Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["privacy"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                PrivacySection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 9. Universal Export Formats Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["export"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                ExportSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 10. Supported Workloads & Real Document Use Cases
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        sectionOffsets["usecases"] = coordinates.positionInParent().y.toInt()
                    }
            ) {
                UseCasesSection(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 11. Final High-Conversion CTA Section
            FinalCtaSection(
                onDownloadClick = { launchApp() },
                onExploreClick = { launchApp() },
                modifier = Modifier.fillMaxWidth()
            )

            // 12. Editorial Footer
            LandingFooter(
                onNavigateSection = { section -> scrollToSection(section) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
