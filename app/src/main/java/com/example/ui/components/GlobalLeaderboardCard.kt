package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.data.model.LeaderboardEntry
import com.example.ui.localization.Strings
import com.example.ui.theme.GameThemePalette

@Composable
fun GlobalLeaderboardCard(
    topPlayers: List<LeaderboardEntry>,
    currentUserId: String,
    lang: AppLanguage,
    palette: GameThemePalette,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val top3 = remember(topPlayers) { topPlayers.take(3) }
    val remainingPlayers = remember(topPlayers) { topPlayers.drop(3).take(7) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(palette.accent, palette.primaryX.copy(alpha = 0.4f), palette.surfaceVariant)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("global_leaderboard_card"),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(palette.accent, palette.accent.copy(alpha = 0.2f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = Strings.topWinsTitle(lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = palette.textPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = Strings.topWinsSubtitle(lang),
                                fontSize = 11.sp,
                                color = palette.textSecondary
                            )
                        }
                    }
                }

                // Expand/Collapse toggle button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .clickable { isExpanded = !isExpanded }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Expand",
                        tint = palette.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Top 3 Podium View
            if (top3.isNotEmpty()) {
                PodiumSection(
                    top3 = top3,
                    currentUserId = currentUserId,
                    lang = lang,
                    palette = palette
                )
            }

            // Expandable List of Remaining Top 10 (Ranks 4 to 10)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "بقية قائمة أفضل 10 (المراكز 4 - 10):" else "Ranks #4 to #10:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    remainingPlayers.forEach { player ->
                        LeaderboardRowItem(
                            entry = player,
                            isCurrentUser = player.userId == currentUserId,
                            lang = lang,
                            palette = palette
                        )
                    }
                }
            }

            // Bottom Footer Row with "View All" Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onViewAll() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = Strings.viewFullLeaderboard(lang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PodiumSection(
    top3: List<LeaderboardEntry>,
    currentUserId: String,
    lang: AppLanguage,
    palette: GameThemePalette
) {
    val first = top3.getOrNull(0)
    val second = top3.getOrNull(1)
    val third = top3.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceVariant.copy(alpha = 0.35f))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // Second Place (Silver)
        if (second != null) {
            PodiumPedestal(
                entry = second,
                rank = 2,
                medal = "🥈",
                badgeColor = Color(0xFF94A3B8),
                pedestalHeight = 60.dp,
                isCurrentUser = second.userId == currentUserId,
                lang = lang,
                palette = palette,
                modifier = Modifier.weight(1f)
            )
        }

        // First Place (Gold - Tallest & Center)
        if (first != null) {
            PodiumPedestal(
                entry = first,
                rank = 1,
                medal = "👑",
                badgeColor = Color(0xFFF59E0B),
                pedestalHeight = 84.dp,
                isCurrentUser = first.userId == currentUserId,
                lang = lang,
                palette = palette,
                modifier = Modifier.weight(1.15f)
            )
        }

        // Third Place (Bronze)
        if (third != null) {
            PodiumPedestal(
                entry = third,
                rank = 3,
                medal = "🥉",
                badgeColor = Color(0xFFB45309),
                pedestalHeight = 48.dp,
                isCurrentUser = third.userId == currentUserId,
                lang = lang,
                palette = palette,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumPedestal(
    entry: LeaderboardEntry,
    rank: Int,
    medal: String,
    badgeColor: Color,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    isCurrentUser: Boolean,
    lang: AppLanguage,
    palette: GameThemePalette,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Medal & Avatar
        Box(
            modifier = Modifier.size(if (rank == 1) 56.dp else 46.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!entry.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = entry.photoUrl,
                    contentDescription = entry.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            width = if (rank == 1) 2.5.dp else 1.5.dp,
                            color = badgeColor,
                            shape = CircleShape
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .border(1.5.dp, badgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(if (rank == 1) 28.dp else 22.dp)
                    )
                }
            }

            // Medal badge pinned
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = medal, fontSize = 11.sp)
            }
        }

        // Name
        Text(
            text = entry.displayName,
            fontSize = if (rank == 1) 12.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = if (isCurrentUser) palette.accent else palette.textPrimary
        )

        // Wins Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.18f))
                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${entry.wins} 🏆",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF10B981)
            )
        }

        // Physical Pedestal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(badgeColor.copy(alpha = 0.35f), badgeColor.copy(alpha = 0.15f))
                    )
                )
                .border(
                    width = 1.dp,
                    color = badgeColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    fontSize = if (rank == 1) 20.sp else 16.sp,
                    fontWeight = FontWeight.Black,
                    color = badgeColor
                )
                Text(
                    text = "${entry.xp} XP",
                    fontSize = 9.sp,
                    color = palette.textSecondary
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRowItem(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    lang: AppLanguage,
    palette: GameThemePalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentUser) palette.accent.copy(alpha = 0.12f) else palette.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = if (isCurrentUser) 1.dp else 0.5.dp,
                color = if (isCurrentUser) palette.accent else palette.gridLine.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(palette.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${entry.rank}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textSecondary
                )
            }

            // Avatar
            if (!entry.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = entry.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(1.dp, palette.accent, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(palette.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Name
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = entry.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isCurrentUser) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "(أنت)" else "(You)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.accent
                        )
                    }
                }
                Text(
                    text = "Lvl ${entry.level} • ${entry.xp} XP",
                    fontSize = 10.sp,
                    color = palette.textSecondary
                )
            }
        }

        // Wins Count
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "${entry.wins} ${if (lang == AppLanguage.ARABIC) "فوز" else "Wins"}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
    }
}
