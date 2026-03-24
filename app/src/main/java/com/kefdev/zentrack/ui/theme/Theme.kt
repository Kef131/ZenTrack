package com.kefdev.zentrack.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = OnSageGreen,
    primaryContainer = SageGreenContainer,
    onPrimaryContainer = OnSageGreenContainer,
    secondary = EarthSand,
    onSecondary = OnEarthSand,
    secondaryContainer = EarthSandContainer,
    onSecondaryContainer = OnEarthSandContainer,
    tertiary = ForestGreen,
    onTertiary = OnForestGreen,
    tertiaryContainer = ForestGreenContainer,
    onTertiaryContainer = OnForestGreenContainer,
    background = WarmBeige,
    onBackground = OnEarthSand,
    surface = WarmWhite,
    onSurface = OnEarthSand,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = OnLightSurfaceVariant,
    outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreenDark,
    onPrimary = OnSageGreenDark,
    primaryContainer = SageGreenContainerDark,
    onPrimaryContainer = OnSageGreenContainerDark,
    secondary = EarthSandDark,
    onSecondary = OnEarthSandDark,
    secondaryContainer = EarthSandContainerDark,
    onSecondaryContainer = OnEarthSandContainerDark,
    tertiary = ForestGreenDark,
    onTertiary = OnForestGreenDark,
    tertiaryContainer = ForestGreenContainerDark,
    onTertiaryContainer = OnForestGreenContainerDark,
    background = DarkBackground,
    onBackground = OnDarkSurfaceVariant,
    surface = DarkSurface,
    onSurface = OnDarkSurfaceVariant,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun ZenTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
