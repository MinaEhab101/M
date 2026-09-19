package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AIDifficulty
import com.example.data.model.AppLanguage
import com.example.data.model.BoardSize
import com.example.data.model.GameMode
import com.example.ui.components.DailyRewardBanner
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.GlobalLeaderboardCard
import com.example.ui.components.GoogleSignInCard
import com.example.ui.localization.Strings
import com.example.ui.theme.GameColors
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigateToGame: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val themeType by viewModel.themeType.collectAsState()
    val palette = GameColors.getPalette(themeType)
    val user by viewModel.currentUser.collectAsState()
    val difficulty by viewModel.aiDifficulty.collectAsState()
    val boardSize by viewModel.boardSize.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val topWinsPlayers by viewModel.topWinsLeaderboard.collectAsState(initial = emptyList())
    val showDailyRewardDialog by viewModel.showDailyRewardDialog.collectAsState()
    val recentDailyRewardClaim by viewModel.recentDailyRewardClaim.collectAsState()

    val scrollState = rememberScrollState()

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Language Switch & Sound & Settings shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceVariant)
                    .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.setLanguage(
                            if (lang == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
                        )
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = palette.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "English" else "العربية",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }
            }

            // Right icons: Sound, Stats, Settings
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onNavigateToStats,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .testTag("stats_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Leaderboard",
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .testTag("settings_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Hero Game Logo & Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(20.dp, CircleShape)
                    .clip(CircleShape)
                    .background(palette.surface)
                    .border(2.dp, palette.primaryX, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_xo_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = Strings.appTitle(lang),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = palette.textPrimary,
                letterSpacing = 1.sp
            )

            // Current Mode Tag (Board size & AI diff)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primaryX.copy(alpha = 0.15f))
                        .clickable { viewModel.increaseBoardSquares() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = Strings.boardSizeLabel(lang, boardSize),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.primaryX
                        )
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = null,
                            tint = palette.primaryX,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primaryO.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = Strings.difficulty(lang, difficulty),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.primaryO
                    )
                }
            }

            // Quick Board Squares Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BoardSize.entries.forEach { size ->
                    val isSelected = boardSize == size
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) palette.accent else palette.surfaceVariant)
                            .clickable { viewModel.setBoardSize(size) }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${size.dimension}×${size.dimension}",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color.Black else palette.textPrimary
                            )
                            Text(
                                text = "${size.totalSquares}■",
                                fontSize = 8.sp,
                                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else palette.textSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Google Sign-In Card / Profile Summary
        GoogleSignInCard(
            user = user,
            lang = lang,
            palette = palette,
            onSignInClick = {
                if (context is Activity) {
                    viewModel.signInWithGoogle(context)
                }
            },
            onSignOutClick = { viewModel.signOut() },
            modifier = Modifier
                .clickable { onNavigateToProfile() }
                .testTag("home_profile_card")
        )

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

        // Global Leaderboard (Top 10 by Total Wins from Firestore)
        GlobalLeaderboardCard(
            topPlayers = topWinsPlayers,
            currentUserId = user.id,
            lang = lang,
            palette = palette,
            onViewAll = onNavigateToStats,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Play Game Options Section
        Text(
            text = if (lang == AppLanguage.ARABIC) "أوضاع اللعب" else "Game Modes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            modifier = Modifier.fillMaxWidth()
        )

        // Mode 1: Play vs AI
        GameModeCard(
            title = Strings.playVsAI(lang),
            subtitle = if (lang == AppLanguage.ARABIC) "تحدَّ الذكاء الاصطناعي بمستويات مختلفة" else "Challenge smart Minimax AI",
            icon = Icons.Default.SmartToy,
            accentColor = palette.primaryX,
            palette = palette,
            onClick = {
                viewModel.startNewGame(
                    mode = GameMode.VS_AI,
                    difficulty = difficulty,
                    size = boardSize,
                    timerSeconds = null
                )
                onNavigateToGame()
            },
            testTag = "play_vs_ai_card"
        )

        // Mode 2: Two Players Local
        GameModeCard(
            title = Strings.twoPlayers(lang),
            subtitle = if (lang == AppLanguage.ARABIC) "العب مع صديقك على نفس الشاشة" else "Pass & play with a friend locally",
            icon = Icons.Default.Group,
            accentColor = palette.primaryO,
            palette = palette,
            onClick = {
                viewModel.startNewGame(
                    mode = GameMode.TWO_PLAYERS,
                    difficulty = difficulty,
                    size = boardSize,
                    timerSeconds = null
                )
                onNavigateToGame()
            },
            testTag = "two_players_card"
        )

        // Mode 3: Speed Challenge (5 seconds per turn)
        GameModeCard(
            title = Strings.speedChallenge(lang),
            subtitle = if (lang == AppLanguage.ARABIC) "سرعة رد الفعل! 5 ثوانٍ فقط لكل حركة" else "Fast paced! Only 5s per turn",
            icon = Icons.Default.Bolt,
            accentColor = palette.accent,
            palette = palette,
            onClick = {
                viewModel.startNewGame(
                    mode = GameMode.VS_AI,
                    difficulty = difficulty,
                    size = boardSize,
                    timerSeconds = 5
                )
                onNavigateToGame()
            },
            testTag = "speed_challenge_card"
        )

        Spacer(modifier = Modifier.height(16.dp))
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
fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    palette: com.example.ui.theme.GameThemePalette,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary
                )
            }
        }
    }
}
