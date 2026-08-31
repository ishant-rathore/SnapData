package com.example.snapdata.ui.components

import android.graphics.RectF
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.DocumentType
import com.example.snapdata.sample.SampleDocument
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.theme.*

import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.components.branding.SnapDataSymbol
import com.example.snapdata.ui.illustrations.EmptyArchiveIllustration
import com.example.snapdata.ui.illustrations.HomeHeroIllustration

/**
 * Editorial Clean Top Header:
 * Left: Official SnapData Logo (Symbol + Wordmark + Tagline)
 * Right: Notification Icon Button + Settings Icon Button
 */
@Composable
fun SnapDataTopHeader(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasUnreadNotification: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Official SnapData Brand Logo
        SnapDataLogo(
            variant = SnapDataLogoVariant.FULL_HORIZONTAL,
            iconSize = 36.dp,
            wordmarkSize = 20.sp,
            taglineSize = 8.5.sp,
            isDarkBackground = false,
            showTagline = true
        )

        // Right Action Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(1.dp, LightBorder, CircleShape)
                    .testTag("top_nav_notifications")
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = SnapDataBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    if (hasUnreadNotification) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SnapDataRed)
                        )
                    }
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(1.dp, LightBorder, CircleShape)
                    .testTag("top_nav_settings")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = SnapDataBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Editorial Hero Greeting Card
 */
@Composable
fun SnapDataHeroCard(
    userName: String,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
            .testTag("hero_greeting_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello, $userName 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ready to extract tables, line items, and structured data from your paper documents.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SnapDataRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = SnapDataRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Minimalist Hand-Drawn Editorial Hero Illustration
            HomeHeroIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                height = 125.dp
            )

            SnapDataPrimaryButton(
                text = "Scan New Document",
                icon = Icons.Default.DocumentScanner,
                onClick = onExploreClick,
                modifier = Modifier.fillMaxWidth(),
                testTag = "hero_scan_btn"
            )
        }
    }
}

/**
 * Editorial Two-Card Main Actions:
 * - Camera Scan (Red icon)
 * - Upload Document (Black icon)
 */
@Composable
fun SnapDataActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isRedIcon: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "action_card"
) {
    Card(
        modifier = modifier
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(18.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isRedIcon) SnapDataRedLight else Color(0xFFF6F4ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isRedIcon) SnapDataRed else SnapDataBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Editorial Overview Section: Total Documents, Accuracy, Total Exports
 */
@Composable
fun SnapDataOverviewSection(
    totalDocuments: Int,
    accuracyPercent: String,
    exportsCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack,
            fontSize = 17.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OverviewMetricCard(
                label = "Documents",
                value = "$totalDocuments",
                icon = Icons.Outlined.Folder,
                modifier = Modifier.weight(1f),
                testTag = "overview_docs_count"
            )
            OverviewMetricCard(
                label = "Accuracy",
                value = accuracyPercent,
                icon = Icons.Outlined.CheckCircle,
                modifier = Modifier.weight(1f),
                isGreen = true,
                testTag = "overview_accuracy"
            )
            OverviewMetricCard(
                label = "Exports",
                value = "$exportsCount",
                icon = Icons.Outlined.FileDownload,
                modifier = Modifier.weight(1f),
                testTag = "overview_exports"
            )
        }
    }
}

@Composable
private fun OverviewMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isGreen: Boolean = false,
    testTag: String = "metric_card"
) {
    Card(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGreen) AccentGreen else TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isGreen) AccentGreen else SnapDataBlack,
                fontSize = 18.sp
            )
        }
    }
}

/**
 * Editorial Document Type Pill Badge
 */
@Composable
fun SnapDataDocumentTypeBadge(
    docType: DocumentType,
    modifier: Modifier = Modifier
) {
    val typeName = when (docType) {
        DocumentType.INVOICE -> "Invoice"
        DocumentType.RECEIPT -> "Receipt"
        DocumentType.BANK_STATEMENT -> "Statement"
        DocumentType.FORM -> "Form"
        DocumentType.CERTIFICATE -> "Certificate"
        DocumentType.MARK_SHEET -> "Mark Sheet"
        DocumentType.ID_CARD -> "ID Card"
        DocumentType.BUSINESS_CARD -> "Card"
        DocumentType.TABLE -> "Table"
        else -> "General"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SnapDataRedLight)
            .border(1.dp, Color(0xFFF5C2C4), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = typeName,
            color = SnapDataRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Editorial Document Card for List / Recent / History
 */
@Composable
fun SnapDataDocumentCard(
    title: String,
    dateFormatted: String,
    docType: DocumentType,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "doc_card"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp, 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFAF7F0))
                    .border(1.dp, SubtleBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = when (docType) {
                            DocumentType.INVOICE -> Icons.Outlined.Receipt
                            DocumentType.RECEIPT -> Icons.Outlined.ReceiptLong
                            DocumentType.BANK_STATEMENT -> Icons.Outlined.AccountBalance
                            else -> Icons.Outlined.Description
                        },
                        contentDescription = null,
                        tint = SnapDataBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(2.dp)
                            .background(SnapDataRed)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            SnapDataDocumentTypeBadge(docType = docType)

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("doc_menu_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Editorial Primary Red Button
 */
@Composable
fun SnapDataPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String = "primary_btn"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SnapDataRed,
            contentColor = CardWhite,
            disabledContainerColor = Color(0xFFECC5C6),
            disabledContentColor = CardWhite
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = CardWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = CardWhite
        )
    }
}

/**
 * Editorial Secondary Outlined Button
 */
@Composable
fun SnapDataSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "secondary_btn"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LightBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor = SnapDataBlack
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = SnapDataBlack)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = SnapDataBlack
        )
    }
}

/**
 * Editorial Sample Document Card
 */
@Composable
fun SnapDataSampleDocumentCard(
    sample: SampleDocument,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "sample_doc_card"
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SnapDataDocumentTypeBadge(docType = sample.type)
                Text(
                    text = "${(sample.confidence * 100).toInt()}% match",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = sample.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${sample.fields.size} fields • ${sample.tables.size} tables",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = SnapDataRed,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Editorial Empty State Component featuring official SnapData Symbol
 */
@Composable
fun SnapDataEmptyState(
    title: String,
    subtitle: String,
    actionLabel: String = "Scan First Document",
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyArchiveIllustration(
            sizeDp = 135.dp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        SnapDataPrimaryButton(
            text = actionLabel,
            icon = Icons.Default.CameraAlt,
            onClick = onAction,
            modifier = Modifier.fillMaxWidth(0.85f),
            testTag = "empty_action_btn"
        )
    }
}
