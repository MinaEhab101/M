package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import coil.compose.AsyncImage
import com.example.data.model.LeaderboardEntry
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MatchRecordEntity
import com.example.data.model.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.GameColors
import com.example.ui.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val themeType by viewModel.themeType.collectAsState()
    val palette = GameColors.getPalette(themeType)
    val user by viewModel.currentUser.collectAsState()
    val historyList by viewModel.historyRecords.collectAsState(initial = emptyList())
    val leaderboard by viewModel.globalLeaderboard.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(palette.background, palette.surface)
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceVariant)
                    .testTag("stats_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary
                )
            }

            Text(
                text = Strings.leaderboards(lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )

            IconButton(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear History",
                    tint = palette.textSecondary
                )
            }
        }

        // Overview Win Rate & Rank Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(palette.surface)
                .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Circular Win Rate
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (user.winRate / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = palette.accent,
                    trackColor = palette.surfaceVariant,
                    strokeWidth = 7.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${user.winRate}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.accent
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "فوز" else "Win",
                        fontSize = 10.sp,
                        color = palette.textSecondary
                    )
                }
            }

            // Stats Breakdown
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.ARABIC) user.rankTitleAr else user.rankTitleEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "${user.wins} فوز" else "${user.wins} Wins",
                        fontSize = 12.sp,
                        color = palette.primaryX,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "${user.draws} تعادل" else "${user.draws} Ties",
                        fontSize = 12.sp,
                        color = palette.accent,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "${user.losses} خسارة" else "${user.losses} Loss",
                        fontSize = 12.sp,
                        color = palette.primaryO,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${user.xp} XP (Lv.${user.level}) • ${user.coins} 🪙 • ${user.loginStreak}d 🔥",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = palette.accent
                    )
                }
            }
        }

        // Segmented Tabs: Match History vs Global Firestore Leaderboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTab == 0) palette.surface else Color.Transparent)
                    .border(
                        width = if (selectedTab == 0) 1.dp else 0.dp,
                        color = if (selectedTab == 0) palette.accent.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("tab_matches")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = if (selectedTab == 0) palette.accent else palette.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "سجل المباريات" else "My Matches",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) palette.textPrimary else palette.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTab == 1) palette.surface else Color.Transparent)
                    .border(
                        width = if (selectedTab == 1) 1.dp else 0.dp,
                        color = if (selectedTab == 1) palette.accent.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("tab_leaderboard")
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        tint = if (selectedTab == 1) palette.accent else palette.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "المتصدرون 🌐" else "Leaderboard 🌐",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) palette.textPrimary else palette.textSecondary
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // History List or Empty State
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surfaceVariant.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = palette.textSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "لا توجد مباريات مسجلة بعد" else "No matches played yet",
                            fontSize = 14.sp,
                            color = palette.textSecondary
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "العب مباراتك الأولى وسجل إنجازاتك!" else "Play your first game to record stats!",
                            fontSize = 12.sp,
                            color = palette.textSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList, key = { it.id }) { record ->
                        MatchHistoryCard(record = record, lang = lang, palette = palette)
                    }
                }
            }
        } else {
            // Global Firestore Leaderboard
            if (leaderboard.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surfaceVariant.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "لوحة المتصدرين السحابية (Firestore)" else "Cloud Firestore Leaderboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "سجل الدخول بحساب Google لتظهر هنا وتنافس الجميع!" else "Sign in with Google to rank on the leaderboard!",
                            fontSize = 12.sp,
                            color = palette.textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(leaderboard, key = { it.userId }) { entry ->
                        LeaderboardCard(entry = entry, isCurrentUser = entry.userId == user.id, palette = palette)
                    }
                }
            }
        }
    }

    // Clear confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "مسح السجل" else "Clear History",
                    color = palette.textPrimary
                )
            },
            text = {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "هل تريد مسح سجل المباريات المحفوظة على هذا الجهاز؟"
                    else
                        "Do you want to clear saved match history from this device?",
                    color = palette.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "مسح" else "Clear",
                        color = Color(0xFFEF4444)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "إلغاء" else "Cancel",
                        color = palette.textPrimary
                    )
                }
            },
            containerColor = palette.surface
        )
    }
}

@Composable
fun MatchHistoryCard(
    record: MatchRecordEntity,
    lang: AppLanguage,
    palette: com.example.ui.theme.GameThemePalette,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(record.timestamp) {
        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        sdf.format(Date(record.timestamp))
    }

    val (outcomeText, outcomeColor) = when (record.winner) {
        "X" -> (if (lang == AppLanguage.ARABIC) "فوز X" else "X Won") to palette.primaryX
        "O" -> (if (lang == AppLanguage.ARABIC) "فوز O" else "O Won") to palette.primaryO
        else -> (if (lang == AppLanguage.ARABIC) "تعادل" else "Draw") to palette.accent
    }

    val modeDesc = if (record.mode == "VS_AI") {
        if (lang == AppLanguage.ARABIC) "ضد الكمبيوتر (${record.aiDifficulty ?: ""})" else "vs AI (${record.aiDifficulty ?: ""})"
    } else {
        if (lang == AppLanguage.ARABIC) "لاعبان" else "2 Players"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.gridLine.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(outcomeColor.copy(alpha = 0.15f))
                        .border(1.dp, outcomeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = outcomeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = outcomeColor
                    )
                }

                Text(
                    text = modeDesc,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.textPrimary
                )
            }

            Text(
                text = "${record.boardDimension}×${record.boardDimension} • ${record.movesCount} نقلة • $dateStr",
                fontSize = 11.sp,
                color = palette.textSecondary
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "+${record.xpEarned} XP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.accent
            )
        }
    }
}

@Composable
fun LeaderboardCard(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    palette: com.example.ui.theme.GameThemePalette,
    modifier: Modifier = Modifier
) {
    val rankBadge = when (entry.rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#${entry.rank}"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCurrentUser) palette.accent.copy(alpha = 0.12f) else palette.surface)
            .border(
                width = if (isCurrentUser) 1.5.dp else 1.dp,
                color = if (isCurrentUser) palette.accent else palette.gridLine.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankBadge,
                    fontSize = if (entry.rank <= 3) 16.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            }

            // Avatar
            val photoUrl = entry.photoUrl
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, palette.accent, CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Name & Level
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = entry.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    if (isCurrentUser) {
                        Text(
                            text = "(أنت)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.accent
                        )
                    }
                }
                Text(
                    text = "المستوى ${entry.level} • ${entry.wins} فوز",
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            }
        }

        // Wins Badge Pill + XP
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.18f))
                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${entry.wins} 🏆",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981)
                )
            }
            Text(
                text = "${entry.xp} XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = palette.accent
            )
        }
    }
}
