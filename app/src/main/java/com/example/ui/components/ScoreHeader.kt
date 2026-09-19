package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AIDifficulty
import com.example.data.model.AppLanguage
import com.example.data.model.GameMode
import com.example.data.model.Player
import com.example.ui.localization.Strings
import com.example.ui.theme.GameThemePalette

@Composable
fun ScoreHeader(
    playerXName: String,
    playerOName: String,
    currentTurn: Player,
    mode: GameMode,
    difficulty: AIDifficulty?,
    scoreX: Int,
    scoreDraw: Int,
    scoreO: Int,
    timeRemainingSeconds: Int?,
    maxTurnTimeSeconds: Int?,
    lang: AppLanguage,
    palette: GameThemePalette,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Player Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player X
            PlayerCard(
                player = Player.X,
                name = playerXName,
                isTurn = currentTurn == Player.X,
                isAi = false,
                symbolColor = palette.primaryX,
                palette = palette,
                modifier = Modifier.weight(1f)
            )

            // Center Scoreboard
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = Strings.score(lang),
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$scoreX - $scoreDraw - $scoreO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.accent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (lang == AppLanguage.ARABIC) "فوز - تعادل - فوز" else "X - Tie - O",
                    fontSize = 9.sp,
                    color = palette.textSecondary.copy(alpha = 0.7f)
                )
            }

            // Player O
            val oDisplayName = if (mode == GameMode.VS_AI) {
                "${Strings.playVsAI(lang)} (${difficulty?.let { Strings.difficulty(lang, it) } ?: ""})"
            } else {
                playerOName
            }

            PlayerCard(
                player = Player.O,
                name = oDisplayName,
                isTurn = currentTurn == Player.O,
                isAi = mode == GameMode.VS_AI,
                symbolColor = palette.primaryO,
                palette = palette,
                modifier = Modifier.weight(1f)
            )
        }

        // Turn Timer Progress (if enabled)
        if (timeRemainingSeconds != null && maxTurnTimeSeconds != null && maxTurnTimeSeconds > 0) {
            val progress = (timeRemainingSeconds.toFloat() / maxTurnTimeSeconds).coerceIn(0f, 1f)
            val timerColor by animateColorAsState(
                targetValue = when {
                    progress > 0.5f -> palette.accent
                    progress > 0.25f -> Color(0xFFF59E0B)
                    else -> Color(0xFFEF4444)
                },
                label = "timerColor"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = timerColor,
                    modifier = Modifier.size(16.dp)
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp)),
                    color = timerColor,
                    trackColor = palette.surfaceVariant
                )
                Text(
                    text = "${timeRemainingSeconds}s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = timerColor
                )
            }
        }
    }
}

@Composable
private fun PlayerCard(
    player: Player,
    name: String,
    isTurn: Boolean,
    isAi: Boolean,
    symbolColor: Color,
    palette: GameThemePalette,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "turnPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val borderColor = if (isTurn) symbolColor else palette.gridLine.copy(alpha = 0.3f)
    val bgColor = if (isTurn) symbolColor.copy(alpha = 0.12f) else palette.surfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .scale(if (isTurn) pulseScale else 1f)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(vertical = 8.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(symbolColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (isAi) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI",
                        tint = symbolColor,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Player",
                        tint = symbolColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = player.symbol,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = symbolColor
            )
        }

        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = if (isTurn) FontWeight.Bold else FontWeight.Normal,
            color = if (isTurn) palette.textPrimary else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        if (isTurn) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(symbolColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "دورك",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
