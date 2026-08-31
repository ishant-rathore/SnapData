package com.example.snapdata.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.ProcessingStage
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.SnapDataPrimaryButton
import com.example.snapdata.ui.components.SnapDataSecondaryButton
import com.example.snapdata.ui.illustrations.ProcessingPipelineIllustration
import com.example.snapdata.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessingScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val progress = uiState.processingProgress
    val isError = progress.stage == ProcessingStage.ERROR

    // Smooth animation for active scanner stage
    val infiniteTransition = rememberInfiniteTransition(label = "processing_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Calculate stage status indices:
    // Stage 1: Pre-processing (ACQUISITION, PREPROCESSING)
    // Stage 2: OCR Processing (OCR, TABLE_DETECTION)
    // Stage 3: AI Analysis (AI_ANALYSIS, STRUCTURED_EXTRACTION)
    // Stage 4: Finalizing (VALIDATION, COMPLETED)
    val currentStageIndex = when (progress.stage) {
        ProcessingStage.IDLE, ProcessingStage.ACQUISITION, ProcessingStage.PREPROCESSING -> 0
        ProcessingStage.OCR, ProcessingStage.TABLE_DETECTION -> 1
        ProcessingStage.AI_ANALYSIS, ProcessingStage.STRUCTURED_EXTRACTION -> 2
        ProcessingStage.VALIDATION, ProcessingStage.COMPLETED -> 3
        ProcessingStage.ERROR, ProcessingStage.CANCELLED -> -1
    }

    val progressPercent = when (currentStageIndex) {
        0 -> 25
        1 -> 50
        2 -> 75
        3 -> 100
        else -> 0
    }

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isError) "Processing Error" else "Processing Document",
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.cancelProcessing()
                            viewModel.navigateTo(AppScreen.HOME)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_processing")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel & Back",
                            tint = SnapDataBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCreamBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Document Title Subtitle
                Text(
                    text = uiState.activeTitle.ifBlank { "Document_001.pdf" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Minimalist Editorial Document Processing Pipeline Illustration
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x06000000))
                        .border(1.dp, LightBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProcessingPipelineIllustration(
                            currentStepIndex = if (isError) 0 else currentStageIndex,
                            height = 95.dp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. Document",
                                fontSize = 10.sp,
                                fontWeight = if (currentStageIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStageIndex == 0) SnapDataRed else TextSecondary
                            )
                            Text(
                                text = "2. Scanner",
                                fontSize = 10.sp,
                                fontWeight = if (currentStageIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStageIndex == 1) SnapDataRed else TextSecondary
                            )
                            Text(
                                text = "3. AI Sparkle",
                                fontSize = 10.sp,
                                fontWeight = if (currentStageIndex == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStageIndex == 2) SnapDataRed else TextSecondary
                            )
                            Text(
                                text = "4. Structured Data",
                                fontSize = 10.sp,
                                fontWeight = if (currentStageIndex >= 3) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentStageIndex >= 3) SnapDataRed else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Error Card if state is ERROR
                if (isError) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF5C2C4), RoundedCornerShape(16.dp))
                            .testTag("processing_error_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SnapDataRedLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Document Extraction Failed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SnapDataRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = progress.error ?: "Unable to read document contents. Please check image clarity and try again.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // 4-Stage Stepper Tracker Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                            .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Step 1: Pre-processing
                            ProcessingStepRow(
                                stepNumber = 1,
                                title = "Pre-processing",
                                subtitle = "Enhancing image quality",
                                isDone = currentStageIndex > 0,
                                isActive = currentStageIndex == 0
                            )

                            // Step 2: OCR Processing
                            ProcessingStepRow(
                                stepNumber = 2,
                                title = "OCR Processing",
                                subtitle = "Extracting text from document",
                                isDone = currentStageIndex > 1,
                                isActive = currentStageIndex == 1
                            )

                            // Step 3: AI Analysis
                            ProcessingStepRow(
                                stepNumber = 3,
                                title = "AI Analysis",
                                subtitle = "Understanding structure & data",
                                isDone = currentStageIndex > 2,
                                isActive = currentStageIndex == 2
                            )

                            // Step 4: Finalizing
                            ProcessingStepRow(
                                stepNumber = 4,
                                title = "Finalizing",
                                subtitle = "Preparing results",
                                isDone = currentStageIndex >= 3,
                                isActive = currentStageIndex == 3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Red Progress Bar + Percentage
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("processing_progress_bar"),
                            color = SnapDataRed,
                            trackColor = Color(0xFFF3EAE4)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$progressPercent% complete",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions
            if (isError) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SnapDataPrimaryButton(
                        text = "Retry Processing",
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.startProcessingPipeline() },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "error_retry_btn"
                    )

                    SnapDataSecondaryButton(
                        text = "Return to Acquisition",
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "error_back_btn"
                    )
                }
            } else {
                SnapDataSecondaryButton(
                    text = "Cancel Extraction",
                    icon = Icons.Default.Close,
                    onClick = { viewModel.cancelProcessing() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "cancel_processing_btn"
                )
            }
        }
    }
}

@Composable
private fun ProcessingStepRow(
    stepNumber: Int,
    title: String,
    subtitle: String,
    isDone: Boolean,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status indicator circle
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> SnapDataRed
                        isActive -> SnapDataRedLight
                        else -> Color(0xFFF5F3ED)
                    }
                )
                .border(
                    width = if (isActive) 1.5.dp else 1.dp,
                    color = if (isActive) SnapDataRed else if (isDone) SnapDataRed else LightBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isDone -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = CardWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
                isActive -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = SnapDataRed
                    )
                }
                else -> {
                    Text(
                        text = stepNumber.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive || isDone) SnapDataBlack else TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
