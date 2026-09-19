package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.model.BoardSize
import com.example.data.model.Achievement
import com.example.data.model.AchievementCatalog
import com.example.data.model.FirestoreSyncStatus
import com.example.data.model.AppLanguage
import com.example.ui.components.DailyRewardBanner
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.localization.Strings
import com.example.ui.theme.GameColors
import com.example.ui.viewmodel.GameViewModel

@Composable
fun ProfileScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val themeType by viewModel.themeType.collectAsState()
    val palette = GameColors.getPalette(themeType)
    val user by viewModel.currentUser.collectAsState()
    val authMsg by viewModel.authMessage.collectAsState()
    val showDailyRewardDialog by viewModel.showDailyRewardDialog.collectAsState()
    val recentDailyRewardClaim by viewModel.recentDailyRewardClaim.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(user.displayName) }

    val scrollState = rememberScrollState()

    val nextLevelXp = user.level * 100
    val currentLevelBaseXp = (user.level - 1) * 100
    val xpProgress = ((user.xp - currentLevelBaseXp).toFloat() / 100f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(palette.background, palette.surface)
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                    .testTag("profile_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary
                )
            }

            Text(
                text = Strings.profile(lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Hero Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, if (user.isGoogleUser) palette.accent else palette.gridLine.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Large Avatar with Google Badge
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .border(3.dp, if (user.isGoogleUser) palette.primaryX else palette.gridLine, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val photoUrl = user.photoUrl
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = palette.textSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                // Name and edit button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = user.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    IconButton(
                        onClick = {
                            editedName = user.displayName
                            showEditDialog = true
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = palette.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                user.email?.let { email ->
                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = palette.textSecondary
                    )
                }

                // Rank Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.accent.copy(alpha = 0.15f))
                        .border(1.dp, palette.accent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) user.rankTitleAr else user.rankTitleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }

                // Currency & Streak Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Coins Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🪙", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "الكوينز" else "Coins",
                                    fontSize = 10.sp,
                                    color = palette.textSecondary
                                )
                                Text(
                                    text = "${user.coins}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    }

                    // Login Streak Pill
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF5722).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFF5722).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🔥", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "أيام الحضور" else "Login Streak",
                                    fontSize = 10.sp,
                                    color = palette.textSecondary
                                )
                                Text(
                                    text = "${user.loginStreak} " + (if (lang == AppLanguage.ARABIC) "أيام" else "Days"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    }
                }

                // Level Progress Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "المستوى ${user.level}" else "Level ${user.level}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = "${user.xp} / $nextLevelXp XP",
                            fontSize = 12.sp,
                            color = palette.textSecondary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = palette.accent,
                        trackColor = palette.surfaceVariant
                    )
                }

                // Google Sign-In or Sign-Out Button
                if (!user.isGoogleUser) {
                    Button(
                        onClick = {
                            if (context is Activity) {
                                viewModel.signInWithGoogle(context)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_google_sign_in")
                    ) {
                        GoogleLogoIcon()
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Strings.googleSignIn(lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.signOut() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("profile_sign_out")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.signOut(lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        // Daily Login Reward Banner
        DailyRewardBanner(
            user = user,
            lang = lang,
            palette = palette,
            onClaimClick = {
                viewModel.claimDailyReward()
                viewModel.openDailyRewardDialog()
            },
            onViewCalendarClick = {
                viewModel.openDailyRewardDialog()
            }
        )

        // ================= LIFETIME GAME STATS SECTION =================
        Text(
            text = if (lang == AppLanguage.ARABIC) "إحصائيات المسيرة الاحترافية الكاملة" else "Lifetime Game Statistics",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = palette.accent
        )

        // Hero Win Rate & Matches Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, palette.accent.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Circular Win Rate Gauge
                    Box(
                        modifier = Modifier.size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { (user.winRate / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            color = palette.accent,
                            trackColor = palette.surfaceVariant,
                            strokeWidth = 9.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${user.winRate}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = palette.textPrimary
                            )
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "نسبة الفوز" else "Win Rate",
                                fontSize = 10.sp,
                                color = palette.textSecondary
                            )
                        }
                    }

                    // Games and XP breakdown summary
                    Column(
                        modifier = Modifier.weight(1f).padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "إجمالي المباريات:" else "Total Matches:",
                                fontSize = 13.sp,
                                color = palette.textSecondary
                            )
                            Text(
                                text = "${user.totalGames}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "نقاط الخبرة XP:" else "Lifetime XP:",
                                fontSize = 13.sp,
                                color = palette.textSecondary
                            )
                            Text(
                                text = "${user.xp} XP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.accent
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "المستوى الحالي:" else "Player Level:",
                                fontSize = 13.sp,
                                color = palette.textSecondary
                            )
                            Text(
                                text = "${user.level}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primaryX
                            )
                        }
                    }
                }

                // Three Column Record Metrics: Wins | Losses | Draws
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LifetimeMetricCard(
                        title = if (lang == AppLanguage.ARABIC) "انتصارات 🏆" else "Wins 🏆",
                        count = user.wins,
                        color = Color(0xFF10B981),
                        palette = palette,
                        modifier = Modifier.weight(1f)
                    )
                    LifetimeMetricCard(
                        title = if (lang == AppLanguage.ARABIC) "هزائم ❌" else "Losses ❌",
                        count = user.losses,
                        color = Color(0xFFEF4444),
                        palette = palette,
                        modifier = Modifier.weight(1f)
                    )
                    LifetimeMetricCard(
                        title = if (lang == AppLanguage.ARABIC) "تعادلات 🤝" else "Draws 🤝",
                        count = user.draws,
                        color = Color(0xFFF59E0B),
                        palette = palette,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Streaks Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "السلسلة الحالية" else "Current Streak",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary
                                )
                                Text(
                                    text = "${user.currentStreak} 🔥",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "أعلى سلسلة تاريخية" else "Best Streak",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary
                                )
                                Text(
                                    text = "${user.bestStreak} ⚡",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Board Squares & Difficulty Mastery Card
        Text(
            text = if (lang == AppLanguage.ARABIC) "سجل شبكات المربعات ومستويات الصعوبة" else "Board Squares & Difficulty Mastery",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "تدعم اللعبة شبكات متعددة تصل إلى 7×7 (49 مربع). كلما زادت المربعات، زادت صعوبة التخطيط والتحدي المطلوب للفوز!"
                    else
                        "The game supports grids up to 7x7 (49 squares). Increasing squares scales strategic difficulty and winning length!",
                    fontSize = 12.sp,
                    color = palette.textSecondary
                )

                BoardSize.entries.forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) size.titleAr else size.titleEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = if (lang == AppLanguage.ARABIC)
                                        "${size.totalSquares} مربع • مطلوب ${size.requiredToWin} للفوز"
                                    else
                                        "${size.totalSquares} squares • ${size.requiredToWin} to win",
                                    fontSize = 10.sp,
                                    color = palette.textSecondary
                                )
                            }
                        }

                        // Difficulty Stars
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (i in 1..size.difficultyRating) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = palette.accent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Career Achievements & Badges (Firestore Synced)
        val allAchievements = remember { AchievementCatalog.allAchievements }
        val unlockedCount = allAchievements.count { user.unlockedAchievements.contains(it.id) || (it.id == AchievementCatalog.FIRST_WIN && user.wins >= 1) || (it.id == AchievementCatalog.WIN_STREAK_3 && user.bestStreak >= 3) || (it.id == AchievementCatalog.WIN_STREAK_10 && user.bestStreak >= 10) }
        val earnedBonusXp = allAchievements.filter { user.unlockedAchievements.contains(it.id) }.sumOf { it.xpReward }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "الإنجازات والأوسمة (Firestore)" else "Achievements & Badges (Firestore)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Text(
                    text = if (lang == AppLanguage.ARABIC) "مزامنة سحابية مع حسابك في Firestore" else "Synced directly to your Firestore profile",
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.accent.copy(alpha = 0.15f))
                    .border(1.dp, palette.accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "$unlockedCount / ${allAchievements.size} 🏆",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.accent
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                allAchievements.forEach { achievement ->
                    val isUnlocked = user.unlockedAchievements.contains(achievement.id) ||
                            (achievement.id == AchievementCatalog.FIRST_WIN && user.wins >= 1) ||
                            (achievement.id == AchievementCatalog.WIN_STREAK_3 && user.bestStreak >= 3) ||
                            (achievement.id == AchievementCatalog.WIN_STREAK_5 && user.bestStreak >= 5) ||
                            (achievement.id == AchievementCatalog.WIN_STREAK_10 && user.bestStreak >= 10) ||
                            (achievement.id == AchievementCatalog.VETERAN_10_GAMES && user.totalGames >= 10) ||
                            (achievement.id == AchievementCatalog.BATTLE_25_GAMES && user.totalGames >= 25) ||
                            (achievement.id == AchievementCatalog.WINS_10 && user.wins >= 10) ||
                            (achievement.id == AchievementCatalog.WINS_25 && user.wins >= 25) ||
                            (achievement.id == AchievementCatalog.LEVEL_5 && user.level >= 5) ||
                            (achievement.id == AchievementCatalog.CLOUD_SYNCED && user.isGoogleUser)

                    val progressValues = AchievementCatalog.getProgressValues(achievement, user)
                    val currentVal = progressValues.first
                    val targetVal = progressValues.second
                    val progressRatio = AchievementCatalog.calculateProgressRatio(achievement, user)

                    AchievementItemRow(
                        achievement = achievement,
                        isUnlocked = isUnlocked,
                        progress = progressRatio,
                        currentVal = currentVal,
                        targetVal = targetVal,
                        lang = lang,
                        palette = palette
                    )
                }
            }
        }

        val syncStatus by viewModel.firestoreSyncStatus.collectAsState()

        // Cloud Firestore Sync Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.primaryX.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when (syncStatus) {
                                FirestoreSyncStatus.SYNCED -> Icons.Default.CloudDone
                                FirestoreSyncStatus.SYNCING -> Icons.Default.Sync
                                else -> Icons.Default.Cloud
                            },
                            contentDescription = null,
                            tint = when (syncStatus) {
                                FirestoreSyncStatus.SYNCED -> Color(0xFF10B981)
                                FirestoreSyncStatus.SYNCING -> palette.accent
                                else -> palette.primaryX
                            },
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "المزامنة السحابية (Cloud Firestore)" else "Cloud Firestore Sync",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (syncStatus) {
                                    FirestoreSyncStatus.SYNCED -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    FirestoreSyncStatus.SYNCING -> palette.accent.copy(alpha = 0.15f)
                                    else -> palette.surfaceVariant
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when (syncStatus) {
                                FirestoreSyncStatus.SYNCED -> if (lang == AppLanguage.ARABIC) "متزامن ✓" else "Synced ✓"
                                FirestoreSyncStatus.SYNCING -> if (lang == AppLanguage.ARABIC) "جاري..." else "Syncing..."
                                else -> if (lang == AppLanguage.ARABIC) "مخزن محلياً" else "Local Cache"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (syncStatus) {
                                FirestoreSyncStatus.SYNCED -> Color(0xFF10B981)
                                FirestoreSyncStatus.SYNCING -> palette.accent
                                else -> palette.textSecondary
                            }
                        )
                    }
                }

                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "يتم حفظ إحصائياتك وسجل مبارياتك تلقائياً في مجموعات Firestore: users/${user.id} و matches."
                    else
                        "Your stats and match history are synchronized automatically to Firestore collections: users/${user.id} and matches.",
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )

                OutlinedButton(
                    onClick = { viewModel.syncNowWithCloud() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "مزامنة سحابية الآن مع Firestore" else "Sync Now to Firestore",
                        fontSize = 13.sp,
                        color = palette.accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Google Play Readiness Checklist
        Text(
            text = if (lang == AppLanguage.ARABIC) "جاهزية متجر Google Play" else "Google Play Store Readiness",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = palette.accent
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "تسجيل الدخول بحساب Google (Credential Manager)" else "Google Sign-In (Credential Manager)",
                    isDone = true,
                    palette = palette
                )
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "قاعدة بيانات سحابية متزامنة (Cloud Firestore)" else "Cloud Firestore Realtime Sync",
                    isDone = true,
                    palette = palette
                )
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "أيقونة التطبيق التكيفية (Adaptive Icon)" else "Adaptive Vector App Icon",
                    isDone = true,
                    palette = palette
                )
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "قاعدة بيانات محلية سريعة (Room Database)" else "Local Room SQLite Database",
                    isDone = true,
                    palette = palette
                )
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "سياسة الخصوصية متوافقة مع Google Play" else "Google Play Compliant Privacy Policy",
                    isDone = true,
                    palette = palette
                )
                PlayReadinessItem(
                    title = if (lang == AppLanguage.ARABIC) "دعم Android 14 و 15 و 16 (Target SDK 36)" else "Android 14/15/16 Ready (Target SDK 36)",
                    isDone = true,
                    palette = palette
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Edit Name Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "تعديل اسم اللاعب" else "Edit Player Name",
                    color = palette.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    singleLine = true,
                    label = { Text(text = if (lang == AppLanguage.ARABIC) "الاسم" else "Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDisplayName(editedName)
                        showEditDialog = false
                    }
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "حفظ" else "Save",
                        color = palette.accent
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "إلغاء" else "Cancel",
                        color = palette.textPrimary
                    )
                }
            },
            containerColor = palette.surface
        )
    }

    if (showDailyRewardDialog) {
        DailyRewardDialog(
            user = user,
            claimResult = recentDailyRewardClaim,
            lang = lang,
            palette = palette,
            onClaimClick = { viewModel.claimDailyReward() },
            onDismiss = { viewModel.dismissDailyRewardDialog() }
        )
    }
}

@Composable
private fun PlayReadinessItem(
    title: String,
    isDone: Boolean,
    palette: com.example.ui.theme.GameThemePalette
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isDone) Color(0xFF10B981).copy(alpha = 0.2f) else palette.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isDone) Color(0xFF10B981) else palette.textSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = title,
            fontSize = 13.sp,
            color = palette.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LifetimeMetricCard(
    title: String,
    count: Int,
    color: Color,
    palette: com.example.ui.theme.GameThemePalette,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun AchievementItemRow(
    achievement: Achievement,
    isUnlocked: Boolean,
    progress: Float,
    currentVal: Int,
    targetVal: Int,
    lang: AppLanguage,
    palette: com.example.ui.theme.GameThemePalette
) {
    val icon = when (achievement.iconName) {
        "emoji_events" -> Icons.Default.EmojiEvents
        "whatshot" -> Icons.Default.Whatshot
        "bolt" -> Icons.Default.Bolt
        "stars" -> Icons.Default.Stars
        "leaderboard" -> Icons.Default.Leaderboard
        "trending_up" -> Icons.Default.TrendingUp
        "grid_on" -> Icons.Default.GridOn
        "shield" -> Icons.Default.Shield
        "star" -> Icons.Default.Star
        "cloud" -> Icons.Default.CloudDone
        else -> Icons.Default.EmojiEvents
    }

    val title = if (lang == AppLanguage.ARABIC) achievement.titleAr else achievement.titleEn
    val desc = if (lang == AppLanguage.ARABIC) achievement.descriptionAr else achievement.descriptionEn

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isUnlocked) palette.accent.copy(alpha = 0.12f) else palette.surfaceVariant.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isUnlocked) palette.accent.copy(alpha = 0.35f) else Color.Transparent,
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) palette.accent else palette.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) Color.Black else palette.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) palette.textPrimary else palette.textSecondary
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isUnlocked) Color(0xFF10B981).copy(alpha = 0.2f) else palette.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${achievement.xpReward} XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color(0xFF10B981) else palette.textSecondary
                    )
                }

                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (!isUnlocked && targetVal > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "التقدم" else "Progress",
                        fontSize = 10.sp,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "$currentVal / $targetVal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textSecondary
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = palette.accent,
                    trackColor = palette.surfaceVariant
                )
            }
        }
    }
}
