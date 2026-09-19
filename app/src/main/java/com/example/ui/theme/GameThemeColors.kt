package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.data.model.GameThemeType

data class GameThemePalette(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primaryX: Color,
    val primaryO: Color,
    val accent: Color,
    val gridLine: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val winningStrike: Color
)

object GameColors {
    val NeonCyber = GameThemePalette(
        background = Color(0xFF070B14),
        surface = Color(0xFF10192A),
        surfaceVariant = Color(0xFF17253D),
        primaryX = Color(0xFF00F5FF),
        primaryO = Color(0xFFFF2A85),
        accent = Color(0xFFFFD700),
        gridLine = Color(0xFF1E3A5F),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFF94A3B8),
        winningStrike = Color(0xFFFFE600)
    )

    val MinimalDark = GameThemePalette(
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B),
        surfaceVariant = Color(0xFF334155),
        primaryX = Color(0xFF38BDF8),
        primaryO = Color(0xFFF43F5E),
        accent = Color(0xFF10B981),
        gridLine = Color(0xFF475569),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        winningStrike = Color(0xFF34D399)
    )

    val ArcadeGold = GameThemePalette(
        background = Color(0xFF140F07),
        surface = Color(0xFF241B0E),
        surfaceVariant = Color(0xFF382B17),
        primaryX = Color(0xFFF59E0B),
        primaryO = Color(0xFF8B5CF6),
        accent = Color(0xFFFBBF24),
        gridLine = Color(0xFF533F1F),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFD97706),
        winningStrike = Color(0xFFFDE047)
    )

    val CosmicPurple = GameThemePalette(
        background = Color(0xFF0B071A),
        surface = Color(0xFF181033),
        surfaceVariant = Color(0xFF281C52),
        primaryX = Color(0xFF2DD4BF),
        primaryO = Color(0xFFA855F7),
        accent = Color(0xFFEC4899),
        gridLine = Color(0xFF3F2D80),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFA855F7),
        winningStrike = Color(0xFFF472B6)
    )

    fun getPalette(type: GameThemeType): GameThemePalette = when (type) {
        GameThemeType.NEON_CYBER -> NeonCyber
        GameThemeType.MINIMAL_DARK -> MinimalDark
        GameThemeType.ARCADE_GOLD -> ArcadeGold
        GameThemeType.COSMIC_PURPLE -> CosmicPurple
    }
}
