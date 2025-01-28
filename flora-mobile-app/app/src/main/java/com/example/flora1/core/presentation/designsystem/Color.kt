package com.example.flora1.core.presentation.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

val FloraRed = Color(0xFFF33168)
val FloraRed60 = Color(0xFFEC849D)
val FloraRed10 = Color(0x1AF33168)

val FloraWhite = Color(0xFFFFFFFF)
val FloraWhite70 = Color(0xB3FFFFFF)
val FloraWhite30 = Color(0x4DFFFFFF)
val FloraWhiteBackground = Color(0xFFFAFAFA)
val FloraDarkGray = Color(0xFF302E2E)

val FloraDarkSurfaceVariant = Color(0xFF2B2C2D)
val FloraLightSurfaceVariant = Color(0xFF302E2E)

val FloraGray = Color(0xFF949494)

val FloraGray30 = Color(0x4D949494)
val FloraGray10 = Color(0x1A949494)

val FloraBlack = Color(0xFF09090A)

val RuniqueWhite = Color(0xFFFAFAFA)


/*
val RuniqueGreen = Color(0xFF00F15E)
val RuniqueGreen30 = Color(0x4D00F15E)
val RuniqueGreen10 = Color(0x1A00F15E)
val RuniqueGreen5 = Color(0x0D00F15E)
val RuniqueBlack = Color(0xFF080707)
val RuniqueGray = Color(0xFF87938C)
val RuniqueGray40 = Color(0x6687938C)
val RuniqueDarkGray = Color(0xFF232624)
val RuniqueWhite = Color(0xFFFAFAFA)
val RuniqueDarkRed = Color(0xFFBB3D3D)
val RuniqueDarkRed5 = Color(0x0DBB3D3D)
 */


/*
OLD COLORS FILE
 */

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xCC625B71)
val Pink40 = Color(0xFF7D5260)

val Orange100 = Color(0xFFF33168)
val Orange60 = Color(0x99F33168)
val Gray80 = Color(0xB3FFFFFF)
val TertiaryGray = Color(0xFF949494)

val GrayColorOnBox = Color(0xFF616161)

val PrimaryVerticalBrush = Brush.verticalGradient(
    listOf<Color>(
        Orange100,
        Color.Transparent
    )
)

const val DisabledAlphaBackground: Float = 0.38f
const val DisabledAlphaText: Float = 0.7f


@Composable
fun getPrimaryHorizontalBrush(isEnabled: Boolean = true) = Brush.horizontalGradient(
    colors = getPrimaryColors(isEnabled = isEnabled),
)

@Composable
fun getPrimaryVerticalBrush(isEnabled: Boolean = true) = Brush.verticalGradient(
    colors = getPrimaryColors(isEnabled = isEnabled),
)

@Composable
fun getPrimaryLinearBrush(isEnabled: Boolean = true) = Brush.linearGradient(
    colors = getPrimaryColors(isEnabled = isEnabled),
)

@Composable
fun getPrimaryRadialBrush(
    isEnabled: Boolean = true,
    center: Offset = Offset.Unspecified,
    radius: Float = Float.POSITIVE_INFINITY,
    tileMode: TileMode = TileMode.Clamp
) = Brush.radialGradient(
    colors = getPrimaryColors(isEnabled = isEnabled),
    center = center,
    radius = radius,
    tileMode = tileMode,
)

@Composable
fun getPrimarySweepBrush(isEnabled: Boolean = true) =
    Brush.sweepGradient(getPrimaryColors(isEnabled = isEnabled))

@Composable
fun getPrimaryColors(isEnabled: Boolean = true) = if (isEnabled)
    enabledPrimaryColors
else
    disabledPrimaryColors

private val enabledPrimaryColors: List<Color>
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.primary,       // Use the primary color from the theme
        MaterialTheme.colorScheme.primaryContainer, // Use the primary container or any other color that fits
    )

private val disabledPrimaryColors: List<Color>
    @Composable
    get() = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = DisabledAlphaBackground),       // Use the primary color from the theme
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = DisabledAlphaBackground), // Use the primary container or any other color that fits
    )
