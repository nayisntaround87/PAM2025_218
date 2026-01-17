package com.example.chillbeads.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Skema warna kustom kita
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    secondary = GradientBlue, // Menggunakan warna biru dari gradasi sebagai warna sekunder
    background = AppBackground,
    surface = AppBackground,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextColor,
    onSurface = TextColor,
)

@Composable
fun ChillBeadsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color dinonaktifkan agar tema kustom kita selalu berlaku
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Untuk saat ini kita hanya fokus pada tema terang (light theme)
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
