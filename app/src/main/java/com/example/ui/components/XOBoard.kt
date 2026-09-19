package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.BoardSize
import com.example.data.model.Player
import com.example.data.model.WinningLine
import com.example.ui.theme.GameThemePalette

@Composable
fun XOBoard(
    board: List<Player?>,
    boardSize: BoardSize,
    winningLine: WinningLine?,
    palette: GameThemePalette,
    enabled: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val n = boardSize.dimension
    val cellSpacing = when {
        n >= 6 -> 3.dp
        n == 5 -> 5.dp
        else -> 8.dp
    }
    val boardPadding = when {
        n >= 6 -> 8.dp
        n == 5 -> 10.dp
        else -> 12.dp
    }
    val cellCornerRadius = when {
        n >= 6 -> 8.dp
        n == 5 -> 12.dp
        else -> 16.dp
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(palette.surface)
            .border(2.dp, palette.gridLine.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .padding(boardPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(cellSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (r in 0 until n) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(cellSpacing)
                ) {
                    for (c in 0 until n) {
                        val index = r * n + c
                        val player = board.getOrNull(index)
                        val isWinningCell = winningLine?.winningCells?.contains(index) == true

                        XOCell(
                            player = player,
                            isWinning = isWinningCell,
                            palette = palette,
                            enabled = enabled && player == null,
                            onClick = { onCellClick(index) },
                            cornerRadius = cellCornerRadius,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .testTag("cell_$index")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun XOCell(
    player: Player?,
    isWinning: Boolean,
    palette: GameThemePalette,
    enabled: Boolean,
    onClick: () -> Unit,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (player != null) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "cellScale"
    )

    val backgroundBrush = remember(isWinning, palette) {
        if (isWinning) {
            Brush.linearGradient(
                colors = listOf(
                    palette.accent.copy(alpha = 0.35f),
                    palette.surfaceVariant
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    palette.surfaceVariant.copy(alpha = 0.9f),
                    palette.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundBrush)
            .border(
                width = if (isWinning) 2.5.dp else 1.dp,
                color = if (isWinning) palette.accent else palette.gridLine.copy(alpha = 0.4f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = palette.accent)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.68f)
                    .scale(scaleAnim)
            ) {
                if (player == Player.X) {
                    XSymbol(color = palette.primaryX)
                } else {
                    OSymbol(color = palette.primaryO)
                }
            }
        }
    }
}

@Composable
fun XSymbol(color: Color, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(220, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = size.width * 0.14f
        val padding = size.width * 0.1f
        val currentProgress = progress.value

        // Line 1: Top-Left to Bottom-Right
        val end1 = Offset(
            x = padding + (size.width - 2 * padding) * currentProgress,
            y = padding + (size.height - 2 * padding) * currentProgress
        )
        drawLine(
            color = color,
            start = Offset(padding, padding),
            end = end1,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Line 2: Top-Right to Bottom-Left
        if (currentProgress > 0.4f) {
            val progress2 = ((currentProgress - 0.4f) / 0.6f).coerceIn(0f, 1f)
            val end2 = Offset(
                x = (size.width - padding) - (size.width - 2 * padding) * progress2,
                y = padding + (size.height - 2 * padding) * progress2
            )
            drawLine(
                color = color,
                start = Offset(size.width - padding, padding),
                end = end2,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun OSymbol(color: Color, modifier: Modifier = Modifier) {
    val sweepProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        sweepProgress.animateTo(360f, animationSpec = tween(260, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = size.width * 0.14f
        val radius = (size.width - strokeWidth) / 2 - (size.width * 0.05f)

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepProgress.value,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
