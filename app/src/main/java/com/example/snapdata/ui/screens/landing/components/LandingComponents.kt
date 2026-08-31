package com.example.snapdata.ui.screens.landing.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.theme.*

@Composable
fun SplashSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOffWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SNAPDATA",
                style = MaterialTheme.typography.displayMedium,
                color = SnapDataBlack,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Smart Document → Structured Data",
                style = MaterialTheme.typography.bodyLarge,
                color = SnapDataRed
            )
        }
    }
}

@Composable
fun HeroSection(onGetStarted: () -> Unit, onSeeHowItWorks: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Turn documents\ninto data.",
            style = MaterialTheme.typography.displayLarge,
            color = SnapDataBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Snap, scan, or import.\nSnapData uses OCR and AI to transform documents into structured, editable information.",
            style = MaterialTheme.typography.bodyLarge,
            color = SnapDataBlack.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onGetStarted,
            colors = ButtonDefaults.buttonColors(containerColor = SnapDataBlack),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Get Started", color = WarmOffWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onSeeHowItWorks,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SnapDataBlack),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("See How It Works", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun WorkflowSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "From document\nto structured data.",
            style = MaterialTheme.typography.displaySmall,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        val steps = listOf("Capture / Import", "OCR", "AI Understanding", "Review & Edit", "Save / Export")
        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "0${index + 1}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = SnapDataRed,
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text = step,
                    style = MaterialTheme.typography.titleLarge,
                    color = SnapDataBlack
                )
            }
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(SnapDataBlack.copy(alpha = 0.2f))
                )
            }
        }
    }
}

@Composable
fun FeatureShowcaseSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        FeatureCard("SEE THE TEXT", "OCR extraction")
        FeatureCard("UNDERSTAND THE DOCUMENT", "AI document analysis")
        FeatureCard("FIND THE DATA", "Key-value extraction")
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineLarge, color = SnapDataBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodyLarge, color = SnapDataRed)
        }
    }
}

@Composable
fun PrivacySection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SnapDataBlack)
            .padding(48.dp)
    ) {
        Column {
            Text(
                text = "Your documents.\nYour device.",
                style = MaterialTheme.typography.displayMedium,
                color = WarmOffWhite
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SnapData is designed offline-first. After the required AI setup, core document processing is intended to run locally on the device without requiring cloud upload.",
                style = MaterialTheme.typography.bodyLarge,
                color = WarmOffWhite.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DemoAndCTASection(onGetStarted: () -> Unit, onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Stop typing.\nStart extracting.",
            style = MaterialTheme.typography.displayMedium,
            color = SnapDataBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onGetStarted,
            colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Get Started", color = WarmOffWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSignIn) {
            Text("Sign In", color = SnapDataBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}
