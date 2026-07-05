package com.chatflow.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PrimaryIndigo = Color(0xFF6366F1)
val PrimaryIndigoLight = Color(0xFF818CF8)

val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF7F7F8)
val LightTextPrimary = Color(0xFF1A1A1A)
val LightTextSecondary = Color(0xFF6B7280)
val LightCodeBg = Color(0xFFF1F1F3)

val DarkBackground = Color(0xFF0F0F10)
val DarkSurface = Color(0xFF1A1A1C)
val DarkTextPrimary = Color(0xFFE5E5E7)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkCodeBg = Color(0xFF18181B)

val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    secondary = PrimaryIndigo,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigoLight,
    secondary = PrimaryIndigoLight,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
)
