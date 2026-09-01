package com.example.snapdata.ui.screens.guide

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel

/**
 * SnapData Interactive User Guide Screen
 * Complete 10-step interactive onboarding experience with spotlight overlays,
 * coach marks, tooltips, animated progress, and completion screen.
 */
@Composable
fun UserGuideScreen(
    viewModel: SnapDataViewModel,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var targetRect by remember { mutableStateOf<Rect?>(null) }
    var showSkipConfirmDialog by remember { mutableStateOf(false) }

    val currentStep = GuideStep.fromIndex(currentStepIndex)
    val isCompletionStep = currentStep == GuideStep.COMPLETION

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    fun nextStep() {
        if (currentStepIndex < 10) {
            targetRect = null
            currentStepIndex++
        } else {
            viewModel.completeUserGuide()
        }
    }

    fun previousStep() {
        if (currentStepIndex > 0) {
            targetRect = null
            currentStepIndex--
        } else {
            viewModel.navigateTo(AppScreen.LANDING)
        }
    }

    fun skipGuide() {
        viewModel.skipUserGuide()
    }

    BackHandler {
        if (showSkipConfirmDialog) {
            showSkipConfirmDialog = false
        } else if (currentStepIndex > 0) {
            previousStep()
        } else {
            viewModel.navigateTo(AppScreen.LANDING)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07080B))
            .testTag("snapdata_user_guide_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header (Steps 1 to 10)
            if (!isCompletionStep) {
                GuideHeader(
                    currentStepIndex = currentStepIndex,
                    totalSteps = 10,
                    onStepClick = { stepIdx ->
                        targetRect = null
                        currentStepIndex = stepIdx
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 2. Interactive Step Screen Container (Responsive Phone Mockup)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(if (isTablet) 0.55f else 0.92f)
                    .padding(vertical = if (isCompletionStep) 16.dp else 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.2.dp, Color(0xFF262834), RoundedCornerShape(22.dp))
                    .shadow(16.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black, spotColor = Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Render the real mock screen for the current step
                AnimatedContent(
                    targetState = currentStepIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { it / 3 })
                                .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutHorizontally(animationSpec = tween(200)) { -it / 3 })
                        } else {
                            (fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { -it / 3 })
                                .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutHorizontally(animationSpec = tween(200)) { it / 3 })
                        }
                    },
                    label = "guide_step_content"
                ) { stepIdx ->
                    when (stepIdx) {
                        0 -> GuideScreen01Home(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        1 -> GuideScreen02Input(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        2 -> GuideScreen03Camera(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        3 -> GuideScreen04Enhancement(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        4 -> GuideScreen05Ocr(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        5 -> GuideScreen06Ai(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        6 -> GuideScreen07Structured(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        7 -> GuideScreen08Review(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        8 -> GuideScreen09Export(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        9 -> GuideScreen10History(
                            onTargetPositioned = { targetRect = it },
                            onTargetClick = { nextStep() }
                        )
                        else -> GuideScreenCompletion(
                            onStartApp = { viewModel.completeUserGuide() }
                        )
                    }
                }

                // Spotlight Overlay for Steps 1-10
                if (!isCompletionStep && targetRect != null) {
                    val isCircleTarget = currentStep == GuideStep.CAMERA_SCANNER
                    SpotlightOverlay(
                        targetBounds = targetRect,
                        isCircle = isCircleTarget,
                        scrimColor = Color(0x66000000),
                        glowColor = Color.White
                    )

                    // Intelligently placed Contextual Tooltip
                    val isTargetNearBottom = (targetRect?.bottom ?: 0f) > 600f
                    Box(
                        modifier = Modifier
                            .align(if (isTargetNearBottom) Alignment.TopCenter else Alignment.BottomCenter)
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        GuideTooltip(
                            text = currentStep.tooltipText,
                            isAboveTarget = isTargetNearBottom
                        )
                    }
                }
            }

            // 3. Bottom Controls (Steps 1 to 10)
            if (!isCompletionStep) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(if (isTablet) 0.55f else 0.92f)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    OutlinedButton(
                        onClick = { previousStep() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333544)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(42.dp).testTag("guide_btn_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentStepIndex > 0) "Back" else "Landing", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Skip Tour Button
                    TextButton(
                        onClick = { showSkipConfirmDialog = true },
                        modifier = Modifier.testTag("guide_btn_skip")
                    ) {
                        Text(
                            text = "Skip Tour",
                            color = Color(0xFFA0A2B0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Next / Finish Button
                    Button(
                        onClick = { nextStep() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(42.dp).testTag("guide_btn_next")
                    ) {
                        Text(
                            text = if (currentStepIndex == 9) "Finish" else "Next",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (currentStepIndex == 9) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Skip Confirmation Dialog
    if (showSkipConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSkipConfirmDialog = false },
            containerColor = Color(0xFF161822),
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Skip the SnapData tour?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "You can always replay the interactive user guide at any time from Settings → Help & Tutorial.",
                    color = Color(0xFFA0A2B0),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipConfirmDialog = false
                        skipGuide()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dialog_confirm_skip_btn")
                ) {
                    Text("Skip", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSkipConfirmDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333544)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("dialog_continue_guide_btn")
                ) {
                    Text("Continue Guide", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                }
            }
        )
    }
}
