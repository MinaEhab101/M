package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.BoardSize
import com.example.ui.theme.GameThemePalette

@Composable
fun BoardSquareController(
    currentSize: BoardSize,
    progressiveScalingEnabled: Boolean,
    onSizeSelected: (BoardSize) -> Unit,
    onIncreaseSquares: () -> Unit,
    onDecreaseSquares: () -> Unit,
    onToggleProgressiveScaling: () -> Unit,
    lang: AppLanguage,
    palette: GameThemePalette,
    escalationNotice: String? = null,
    onDismissEscalationNotice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, palette.accent.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Title & Difficulty Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "التحكم في مربعات وصعوبة اللعب" else "Board Squares & Difficulty",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }

                // Star Rating
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= currentSize.difficultyRating) palette.accent else palette.surfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Escalation Notice Banner
            AnimatedVisibility(
                visible = escalationNotice != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                escalationNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.accent.copy(alpha = 0.2f))
                            .border(1.dp, palette.accent, RoundedCornerShape(12.dp))
                            .clickable { onDismissEscalationNotice() }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.accent,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Stepper Row: [-] [ Current Square Info ] [+]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Decrease Button
                val canDecrease = currentSize.nextSmallerSize() != null
                IconButton(
                    onClick = onDecreaseSquares,
                    enabled = canDecrease,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (canDecrease) palette.surface else Color.Transparent)
                        .testTag("btn_decrease_squares")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Squares",
                        tint = if (canDecrease) palette.textPrimary else palette.textSecondary.copy(alpha = 0.4f)
                    )
                }

                // Middle: Dimension, Total Squares & Rule
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) currentSize.titleAr else currentSize.titleEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.accent
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC)
                            "صعوبة: ${currentSize.difficultyLabelAr} • مطلوب ${currentSize.requiredToWin} في خط واحد للفوز"
                        else
                            "Difficulty: ${currentSize.difficultyLabelEn} • ${currentSize.requiredToWin} in a line to win",
                        fontSize = 11.sp,
                        color = palette.textSecondary
                    )
                }

                // Increase Button
                val canIncrease = currentSize.nextBiggerSize() != null
                IconButton(
                    onClick = onIncreaseSquares,
                    enabled = canIncrease,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (canIncrease) palette.surface else Color.Transparent)
                        .testTag("btn_increase_squares")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Squares",
                        tint = if (canIncrease) palette.accent else palette.textSecondary.copy(alpha = 0.4f)
                    )
                }
            }

            // Quick Chips Row: [3x3 (9)] [4x4 (16)] [5x5 (25)] [6x6 (36)] [7x7 (49)]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BoardSize.entries.forEach { size ->
                    val isSelected = currentSize == size
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) palette.accent else palette.surfaceVariant)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) palette.textPrimary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSizeSelected(size) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${size.dimension}×${size.dimension}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color.Black else palette.textPrimary
                            )
                            Text(
                                text = "${size.totalSquares}■",
                                fontSize = 9.sp,
                                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else palette.textSecondary
                            )
                        }
                    }
                }
            }

            // Progressive Auto-Escalation Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.4f))
                    .clickable { onToggleProgressiveScaling() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = if (progressiveScalingEnabled) palette.accent else palette.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "تصعيد المربعات التلقائي مع كل فوز 🔥" else "Progressive Square Scaling on Win 🔥",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "زيادة حجم الشبكة وصعوبة اللعبة تلقائياً عند هزيمة الكمبيوتر" else "Auto-increases board squares and difficulty upon beating AI",
                            fontSize = 10.sp,
                            color = palette.textSecondary
                        )
                    }
                }

                Switch(
                    checked = progressiveScalingEnabled,
                    onCheckedChange = { onToggleProgressiveScaling() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = palette.accent,
                        checkedTrackColor = palette.surfaceVariant,
                        uncheckedThumbColor = palette.textSecondary,
                        uncheckedTrackColor = palette.surfaceVariant
                    ),
                    modifier = Modifier.testTag("switch_progressive_scaling")
                )
            }
        }
    }
}
