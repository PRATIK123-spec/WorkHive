package com.example.workhive.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7C4DFF),
    onPrimary = Color.White,
    surface = Color(0xFF111318),
    onSurface = Color.White,
    background = Color(0xFF0F141E)
)

@Composable
fun WorkHiveTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography(), content = content)
}
