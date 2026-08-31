package com.example.snapdata.ui.components.branding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.R
import com.example.snapdata.ui.theme.SnapDataRed

/**
 * SnapData Official Branding Variants:
 * 1. FULL_HORIZONTAL: Icon + SnapData + Tagline
 * 2. FULL_VERTICAL: Large Icon + Centered SnapData + Tagline (Splash/Hero)
 * 3. ICON_ONLY: Document + Data Network Pattern + Shutter Symbol
 * 4. WHITE_ON_DARK: Primary branding on dark surfaces
 * 5. RED_ON_DARK: Accent variant with vivid red outlines & nodes
 * 6. MONOCHROME: Clean white/black contrast variant
 * 7. SMALL_ICON: Simplified for small UI elements & chips
 */
enum class SnapDataLogoVariant {
    FULL_HORIZONTAL,
    FULL_VERTICAL,
    ICON_ONLY,
    WHITE_ON_DARK,
    RED_ON_DARK,
    MONOCHROME,
    SMALL_ICON
}

/**
 * Renders the official SnapData Symbol (Document + Folded Corner + Circuit Grid + Camera Shutter).
 */
@Composable
fun SnapDataSymbol(
    modifier: Modifier = Modifier,
    variant: SnapDataLogoVariant = SnapDataLogoVariant.WHITE_ON_DARK,
    size: Dp = 48.dp,
    containerBackground: Color? = null,
    showContainerCard: Boolean = false
) {
    val drawableRes = when (variant) {
        SnapDataLogoVariant.RED_ON_DARK -> R.drawable.ic_snapdata_symbol_red
        SnapDataLogoVariant.MONOCHROME -> R.drawable.ic_snapdata_symbol_monochrome
        SnapDataLogoVariant.SMALL_ICON -> R.drawable.ic_snapdata_symbol_small
        else -> R.drawable.ic_snapdata_symbol
    }

    if (showContainerCard) {
        Box(
            modifier = modifier
                .size(size + 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(containerBackground ?: Color(0xFF0F1014))
                .border(1.dp, Color(0xFF262830), RoundedCornerShape(14.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "SnapData Logo Symbol",
                modifier = Modifier.size(size)
            )
        }
    } else {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "SnapData Logo Symbol",
            modifier = modifier.size(size)
        )
    }
}

/**
 * Renders the official "SnapData" Wordmark with "Snap" (Adaptive) and "Data" (Vivid Red).
 */
@Composable
fun SnapDataWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    snapColor: Color = Color.White,
    dataColor: Color = SnapDataRed,
    letterSpacing: TextUnit = (-0.5).sp,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = snapColor,
                    fontWeight = fontWeight,
                    letterSpacing = letterSpacing
                )
            ) {
                append("Snap")
            }
            withStyle(
                style = SpanStyle(
                    color = dataColor,
                    fontWeight = fontWeight,
                    letterSpacing = letterSpacing
                )
            ) {
                append("Data")
            }
        },
        fontSize = fontSize,
        fontFamily = FontFamily.SansSerif,
        modifier = modifier.testTag("snapdata_wordmark")
    )
}

/**
 * Renders the official Tagline: "AI-POWERED DOCUMENT INTELLIGENCE"
 */
@Composable
fun SnapDataTagline(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 10.sp,
    color: Color = Color(0xFFA0A0A5),
    letterSpacing: TextUnit = 1.6.sp,
    fontWeight: FontWeight = FontWeight.SemiBold
) {
    Text(
        text = "AI-POWERED DOCUMENT INTELLIGENCE",
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        letterSpacing = letterSpacing,
        fontFamily = FontFamily.SansSerif,
        modifier = modifier.testTag("snapdata_tagline")
    )
}

/**
 * Master SnapData Brand Component supporting all 6 official variants.
 */
@Composable
fun SnapDataLogo(
    modifier: Modifier = Modifier,
    variant: SnapDataLogoVariant = SnapDataLogoVariant.FULL_HORIZONTAL,
    iconSize: Dp = 42.dp,
    wordmarkSize: TextUnit = 22.sp,
    taglineSize: TextUnit = 9.sp,
    isDarkBackground: Boolean = true,
    showTagline: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val snapTextColor = if (isDarkBackground) Color.White else Color(0xFF141414)
    val taglineColor = if (isDarkBackground) Color(0xFFA0A0A5) else Color(0xFF6B6B6B)

    val baseModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    when (variant) {
        SnapDataLogoVariant.FULL_HORIZONTAL -> {
            Row(
                modifier = baseModifier.testTag("snapdata_logo_horizontal"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SnapDataSymbol(
                    size = iconSize,
                    variant = if (isDarkBackground) SnapDataLogoVariant.WHITE_ON_DARK else SnapDataLogoVariant.WHITE_ON_DARK
                )

                Column(verticalArrangement = Arrangement.Center) {
                    SnapDataWordmark(
                        fontSize = wordmarkSize,
                        snapColor = snapTextColor,
                        dataColor = SnapDataRed
                    )
                    if (showTagline) {
                        Spacer(modifier = Modifier.height(2.dp))
                        SnapDataTagline(
                            fontSize = taglineSize,
                            color = taglineColor
                        )
                    }
                }
            }
        }

        SnapDataLogoVariant.FULL_VERTICAL -> {
            Column(
                modifier = baseModifier.testTag("snapdata_logo_vertical"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SnapDataSymbol(
                    size = iconSize,
                    variant = SnapDataLogoVariant.WHITE_ON_DARK
                )
                Spacer(modifier = Modifier.height(16.dp))
                SnapDataWordmark(
                    fontSize = wordmarkSize,
                    snapColor = snapTextColor,
                    dataColor = SnapDataRed
                )
                if (showTagline) {
                    Spacer(modifier = Modifier.height(6.dp))
                    SnapDataTagline(
                        fontSize = taglineSize,
                        color = taglineColor
                    )
                }
            }
        }

        SnapDataLogoVariant.ICON_ONLY -> {
            SnapDataSymbol(
                modifier = baseModifier.testTag("snapdata_logo_icon_only"),
                size = iconSize,
                variant = SnapDataLogoVariant.WHITE_ON_DARK
            )
        }

        SnapDataLogoVariant.WHITE_ON_DARK -> {
            Row(
                modifier = baseModifier.testTag("snapdata_logo_white_on_dark"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnapDataSymbol(size = iconSize, variant = SnapDataLogoVariant.WHITE_ON_DARK)
                Column {
                    SnapDataWordmark(fontSize = wordmarkSize, snapColor = Color.White, dataColor = SnapDataRed)
                    if (showTagline) {
                        SnapDataTagline(fontSize = taglineSize, color = Color(0xFFA0A0A5))
                    }
                }
            }
        }

        SnapDataLogoVariant.RED_ON_DARK -> {
            Row(
                modifier = baseModifier.testTag("snapdata_logo_red_on_dark"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnapDataSymbol(size = iconSize, variant = SnapDataLogoVariant.RED_ON_DARK)
                Column {
                    SnapDataWordmark(fontSize = wordmarkSize, snapColor = Color.White, dataColor = SnapDataRed)
                    if (showTagline) {
                        SnapDataTagline(fontSize = taglineSize, color = SnapDataRed)
                    }
                }
            }
        }

        SnapDataLogoVariant.MONOCHROME -> {
            Row(
                modifier = baseModifier.testTag("snapdata_logo_monochrome"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnapDataSymbol(size = iconSize, variant = SnapDataLogoVariant.MONOCHROME)
                Column {
                    SnapDataWordmark(fontSize = wordmarkSize, snapColor = Color.White, dataColor = Color(0xFFD4D4D8))
                    if (showTagline) {
                        SnapDataTagline(fontSize = taglineSize, color = Color(0xFFA1A1AA))
                    }
                }
            }
        }

        SnapDataLogoVariant.SMALL_ICON -> {
            SnapDataSymbol(
                modifier = baseModifier.testTag("snapdata_logo_small_icon"),
                size = iconSize,
                variant = SnapDataLogoVariant.SMALL_ICON
            )
        }
    }
}
