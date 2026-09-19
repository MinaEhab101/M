package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.DailyRewardClaimResult
import com.example.data.model.DailyRewardDay
import com.example.data.model.DailyRewardSchedule
import com.example.data.model.UserProfile
import com.example.ui.localization.Strings
import com.example.ui.theme.GameThemePalette

@Composable
fun DailyRewardBanner(
    user: UserProfile,
    lang: AppLanguage,
    palette: GameThemePalette,
    onClaimClick: () -> Unit,
    onViewCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canClaim = remember(user.lastRewardDate) { DailyRewardSchedule.canClaimToday(user) }
    val targetDay = remember(user.lastRewardDate, user.loginStreak) { DailyRewardSchedule.getTargetDay(user) }
    val nextReward = remember(targetDay) { DailyRewardSchedule.getRewardForDay(targetDay) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (canClaim) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (canClaim) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    if (canClaim) listOf(Color(0xFFFFD700), palette.accent)
                    else listOf(palette.gridLine.copy(alpha = 0.5f), palette.surfaceVariant)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onViewCalendarClick() }
            .testTag("daily_reward_banner"),
        colors = CardDefaults.cardColors(
            containerColor = palette.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Title & Streak Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (canClaim) Color(0xFFFFD700).copy(alpha = 0.2f)
                                else palette.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Reward",
                            tint = if (canClaim) Color(0xFFFFD700) else palette.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = Strings.dailyRewardTitle(lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = if (canClaim) {
                                if (lang == AppLanguage.ARABIC) "المكافأة متاحة الآن! (+${nextReward.coins} 🪙، +${nextReward.xp} XP)"
                                else "Reward ready! (+${nextReward.coins} 🪙, +${nextReward.xp} XP)"
                            } else {
                                if (lang == AppLanguage.ARABIC) "تم استلام مكافأة اليوم! نراك غداً"
                                else "Claimed today! See you tomorrow"
                            },
                            fontSize = 11.sp,
                            color = if (canClaim) palette.accent else palette.textSecondary
                        )
                    }
                }

                // Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.accent.copy(alpha = 0.15f))
                        .border(1.dp, palette.accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${user.loginStreak}d",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.accent
                        )
                    }
                }
            }

            // 7-Day Mini Track Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DailyRewardSchedule.rewards.forEach { dayReward ->
                    val isPastClaimed = if (canClaim) {
                        dayReward.dayNumber < targetDay
                    } else {
                        dayReward.dayNumber <= targetDay
                    }
                    val isCurrentDay = dayReward.dayNumber == targetDay

                    MiniDayIndicator(
                        day = dayReward,
                        isCurrent = isCurrentDay,
                        isClaimed = isPastClaimed,
                        canClaim = canClaim,
                        palette = palette,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Action Button
            if (canClaim) {
                Button(
                    onClick = onClaimClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .scale(pulseScale)
                        .testTag("claim_daily_reward_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "استلام مكافأة اليوم $targetDay (+${nextReward.coins} 🪙 / +${nextReward.xp} XP)"
                        else "Claim Day $targetDay (+${nextReward.coins} 🪙 / +${nextReward.xp} XP)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onViewCalendarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("view_calendar_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = palette.textSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.dailyRewardClaimed(lang) + "  •  " + Strings.dayLabel(lang, targetDay),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniDayIndicator(
    day: DailyRewardDay,
    isCurrent: Boolean,
    isClaimed: Boolean,
    canClaim: Boolean,
    palette: GameThemePalette,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isClaimed -> Color(0xFF10B981).copy(alpha = 0.2f)
        isCurrent && canClaim -> Color(0xFFFFD700).copy(alpha = 0.25f)
        isCurrent -> palette.accent.copy(alpha = 0.2f)
        day.isJackpot -> Color(0xFFFFD700).copy(alpha = 0.1f)
        else -> palette.surfaceVariant.copy(alpha = 0.5f)
    }

    val borderColor = when {
        isClaimed -> Color(0xFF10B981)
        isCurrent && canClaim -> Color(0xFFFFD700)
        isCurrent -> palette.accent
        day.isJackpot -> Color(0xFFFFD700).copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "D${day.dayNumber}",
            fontSize = 9.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) palette.textPrimary else palette.textSecondary
        )
        if (isClaimed) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(12.dp)
            )
        } else {
            Text(
                text = "${day.coins}🪙",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (day.isJackpot) Color(0xFFFFD700) else palette.accent
            )
        }
    }
}

@Composable
fun DailyRewardDialog(
    user: UserProfile,
    claimResult: DailyRewardClaimResult?,
    lang: AppLanguage,
    palette: GameThemePalette,
    onClaimClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val canClaim = remember(user.lastRewardDate) { DailyRewardSchedule.canClaimToday(user) }
    val targetDay = remember(user.lastRewardDate, user.loginStreak) { DailyRewardSchedule.getTargetDay(user) }
    val nextReward = remember(targetDay) { DailyRewardSchedule.getRewardForDay(targetDay) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, palette.accent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("daily_reward_dialog"),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFD700).copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                        .border(2.dp, Color(0xFFFFD700), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Daily Rewards",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = Strings.dailyRewardTitle(lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = Strings.loginStreakText(lang, user.loginStreak) + " • " + (if (lang == AppLanguage.ARABIC) "مزامنة Firestore السحابية" else "Firestore Cloud Sync"),
                        fontSize = 12.sp,
                        color = palette.accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Celebratory result banner if just claimed
                AnimatedVisibility(
                    visible = claimResult != null,
                    enter = fadeIn() + scaleIn()
                ) {
                    if (claimResult != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "🎉 مبروك! استلمت مكافأة اليوم ${claimResult.dayNumber}"
                                else "🎉 Congratulations! Day ${claimResult.dayNumber} Claimed!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "+${claimResult.coinsEarned} 🪙 كوينز  •  +${claimResult.xpEarned} ⚡ XP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = palette.textPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "تم التحديث السحابي في Firestore" else "Synced with Cloud Firestore",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary
                                )
                            }
                        }
                    }
                }

                // 7-Day Rewards List / Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DailyRewardSchedule.rewards.forEach { dayReward ->
                        val isPastClaimed = if (canClaim) {
                            dayReward.dayNumber < targetDay
                        } else {
                            dayReward.dayNumber <= targetDay
                        }
                        val isCurrentDay = dayReward.dayNumber == targetDay

                        DayRewardRow(
                            day = dayReward,
                            isCurrent = isCurrentDay,
                            isClaimed = isPastClaimed,
                            canClaim = canClaim,
                            lang = lang,
                            palette = palette
                        )
                    }
                }

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canClaim) {
                        Button(
                            onClick = onClaimClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("dialog_claim_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "استلام مكافأة اليوم $targetDay (+${nextReward.coins} 🪙 / +${nextReward.xp} XP)"
                                else "Claim Day $targetDay (+${nextReward.coins} 🪙 / +${nextReward.xp} XP)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("dialog_dismiss_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "إغلاق" else "Close",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayRewardRow(
    day: DailyRewardDay,
    isCurrent: Boolean,
    isClaimed: Boolean,
    canClaim: Boolean,
    lang: AppLanguage,
    palette: GameThemePalette
) {
    val bgColor = when {
        isClaimed -> Color(0xFF10B981).copy(alpha = 0.12f)
        isCurrent && canClaim -> Color(0xFFFFD700).copy(alpha = 0.18f)
        isCurrent -> palette.accent.copy(alpha = 0.12f)
        day.isJackpot -> Color(0xFFFFD700).copy(alpha = 0.08f)
        else -> palette.surfaceVariant.copy(alpha = 0.4f)
    }

    val borderColor = when {
        isClaimed -> Color(0xFF10B981).copy(alpha = 0.4f)
        isCurrent && canClaim -> Color(0xFFFFD700)
        isCurrent -> palette.accent
        day.isJackpot -> Color(0xFFFFD700).copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (day.isJackpot) Color(0xFFFFD700)
                        else if (isClaimed) Color(0xFF10B981)
                        else palette.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isClaimed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Claimed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (day.isJackpot) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Jackpot",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "${day.dayNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = Strings.dayLabel(lang, day.dayNumber),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    if (day.isJackpot) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFD700))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = Strings.jackpot(lang),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
                Text(
                    text = if (lang == AppLanguage.ARABIC) day.descriptionAr else day.descriptionEn,
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )
            }
        }

        // Status on right
        if (isClaimed) {
            Text(
                text = if (lang == AppLanguage.ARABIC) "مستلمة ✅" else "Claimed ✅",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        } else if (isCurrent && canClaim) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFD700))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "جاهز! 🎁" else "Ready! 🎁",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = palette.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
