package com.lonwulf.labs.easyshopmanager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private val highContrastLightColorScheme = lightColorScheme(
    primary = PrimaryLightHighContrast,
    onPrimary = OnPrimaryLightHighContrast,
    primaryContainer = PrimaryContainerLightHighContrast,
    onPrimaryContainer = OnPrimaryContainerLightHighContrast,
    inversePrimary = InversePrimaryLightHighContrast,
    secondary = SecondaryLightHighContrast,
    onSecondary = OnSecondaryLightHighContrast,
    secondaryContainer = SecondaryContainerLightHighContrast,
    onSecondaryContainer = OnSecondaryContainerLightHighContrast,
    tertiary = TertiaryLightHighContrast,
    onTertiary = OnTertiaryLightHighContrast,
    tertiaryContainer = TertiaryContainerLightHighContrast,
    onTertiaryContainer = OnTertiaryContainerLightHighContrast,
    background = BackgroundLightHighContrast,
    onBackground = OnBackgroundLightHighContrast,
    surface = SurfaceLightHighContrast,
    onSurface = OnSurfaceLightHighContrast,
    surfaceVariant = SurfaceVariantLightHighContrast,
    onSurfaceVariant = OnSurfaceVariantLightHighContrast,
    surfaceTint = SurfaceTintLightHighContrast,
    inverseSurface = InverseSurfaceLightHighContrast,
    inverseOnSurface = InverseOnSurfaceLightHighContrast,
    error = ErrorLightHighContrast,
    onError = OnErrorLightHighContrast,
    errorContainer = ErrorContainerLightHighContrast,
    onErrorContainer = OnErrorContainerLightHighContrast,
    outline = OutlineLightHighContrast,
    outlineVariant = OutlineVariantLightHighContrast,
    scrim = ScrimLightHighContrast,
    surfaceBright = SurfaceBrightLightHighContrast,
    surfaceContainer = SurfaceContainerLightHighContrast,
    surfaceContainerHigh = SurfaceContainerHighLightHighContrast,
    surfaceContainerHighest = SurfaceContainerHighestLightHighContrast,
    surfaceContainerLow = SurfaceContainerLowLightHighContrast,
    surfaceContainerLowest = SurfaceContainerLowestLightHighContrast,
    surfaceDim = SurfaceDimLightHighContrast,
    primaryFixed = PrimaryFixedHighContrast,
    primaryFixedDim = PrimaryFixedDimHighContrast,
    onPrimaryFixed = OnPrimaryFixedHighContrast,
    onPrimaryFixedVariant = OnPrimaryFixedVariantHighContrast,
    secondaryFixed = SecondaryFixedHighContrast,
    secondaryFixedDim = SecondaryFixedDimHighContrast,
    onSecondaryFixed = OnSecondaryFixedHighContrast,
    onSecondaryFixedVariant = OnSecondaryFixedVariantHighContrast,
    tertiaryFixed = TertiaryFixedHighContrast,
    tertiaryFixedDim = TertiaryFixedDimHighContrast,
    onTertiaryFixed = OnTertiaryFixedHighContrast,
    onTertiaryFixedVariant = OnTertiaryFixedVariantHighContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = PrimaryDarkHighContrast,
    onPrimary = OnPrimaryDarkHighContrast,
    primaryContainer = PrimaryContainerDarkHighContrast,
    onPrimaryContainer = OnPrimaryContainerDarkHighContrast,
    inversePrimary = InversePrimaryDarkHighContrast,
    secondary = SecondaryDarkHighContrast,
    onSecondary = OnSecondaryDarkHighContrast,
    secondaryContainer = SecondaryContainerDarkHighContrast,
    onSecondaryContainer = OnSecondaryContainerDarkHighContrast,
    tertiary = TertiaryDarkHighContrast,
    onTertiary = OnTertiaryDarkHighContrast,
    tertiaryContainer = TertiaryContainerDarkHighContrast,
    onTertiaryContainer = OnTertiaryContainerDarkHighContrast,
    background = BackgroundDarkHighContrast,
    onBackground = OnBackgroundDarkHighContrast,
    surface = SurfaceDarkHighContrast,
    onSurface = OnSurfaceDarkHighContrast,
    surfaceVariant = SurfaceVariantDarkHighContrast,
    onSurfaceVariant = OnSurfaceVariantDarkHighContrast,
    surfaceTint = SurfaceTintDarkHighContrast,
    inverseSurface = InverseSurfaceDarkHighContrast,
    inverseOnSurface = InverseOnSurfaceDarkHighContrast,
    error = ErrorDarkHighContrast,
    onError = OnErrorDarkHighContrast,
    errorContainer = ErrorContainerDarkHighContrast,
    onErrorContainer = OnErrorContainerDarkHighContrast,
    outline = OutlineDarkHighContrast,
    outlineVariant = OutlineVariantDarkHighContrast,
    scrim = ScrimDarkHighContrast,
    surfaceBright = SurfaceBrightDarkHighContrast,
    surfaceContainer = SurfaceContainerDarkHighContrast,
    surfaceContainerHigh = SurfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = SurfaceContainerHighestDarkHighContrast,
    surfaceContainerLow = SurfaceContainerLowDarkHighContrast,
    surfaceContainerLowest = SurfaceContainerLowestDarkHighContrast,
    surfaceDim = SurfaceDimDarkHighContrast,
    primaryFixed = PrimaryFixedHighContrast,
    primaryFixedDim = PrimaryFixedDimHighContrast,
    onPrimaryFixed = OnPrimaryFixedHighContrast,
    onPrimaryFixedVariant = OnPrimaryFixedVariantHighContrast,
    secondaryFixed = SecondaryFixedHighContrast,
    secondaryFixedDim = SecondaryFixedDimHighContrast,
    onSecondaryFixed = OnSecondaryFixedHighContrast,
    onSecondaryFixedVariant = OnSecondaryFixedVariantHighContrast,
    tertiaryFixed = TertiaryFixedHighContrast,
    tertiaryFixedDim = TertiaryFixedDimHighContrast,
    onTertiaryFixed = OnTertiaryFixedHighContrast,
    onTertiaryFixedVariant = OnTertiaryFixedVariantHighContrast,
)

@Composable
fun EasyShopManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> highContrastDarkColorScheme
        else -> highContrastLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}