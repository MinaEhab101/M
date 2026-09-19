package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameSoundEffect
import com.example.data.model.AIDifficulty
import com.example.data.model.AppLanguage
import com.example.data.model.BoardSize
import com.example.data.model.GameThemeType
import com.example.ui.localization.Strings
import com.example.ui.theme.GameColors
import com.example.ui.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val themeType by viewModel.themeType.collectAsState()
    val palette = GameColors.getPalette(themeType)

    val difficulty by viewModel.aiDifficulty.collectAsState()
    val boardSize by viewModel.boardSize.collectAsState()
    val maxTurnTime by viewModel.maxTurnTimerSeconds.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val soundVolume by viewModel.soundVolume.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()

    var lastPreviewedEffect by remember { mutableStateOf<GameSoundEffect?>(null) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary
                )
            }

            Text(
                text = Strings.settings(lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Section: Game Rules & AI
        SettingsSectionTitle(title = if (lang == AppLanguage.ARABIC) "إعدادات اللعب والذكاء الاصطناعي" else "Game Rules & AI", palette = palette)

        // Difficulty Selector
        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "مستوى الذكاء الاصطناعي" else "AI Difficulty",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AIDifficulty.entries.forEach { diff ->
                        val isSelected = difficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) palette.accent else palette.surfaceVariant)
                                .clickable { viewModel.startNewGame(difficulty = diff) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = Strings.difficulty(lang, diff),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.Black else palette.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Board Size Selector & Squares Controller
        val progressiveScalingEnabled by viewModel.progressiveScalingEnabled.collectAsState()

        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "حجم الشبكة وعدد المربعات" else "Grid Size & Squares",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "${boardSize.totalSquares} ${if (lang == AppLanguage.ARABIC) "مربع" else "squares"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BoardSize.entries.forEach { size ->
                        val isSelected = boardSize == size
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) palette.accent else palette.surfaceVariant)
                                .clickable { viewModel.startNewGame(size = size) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${size.dimension}×${size.dimension}",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black else palette.textPrimary
                                )
                                Text(
                                    text = "${size.totalSquares}■",
                                    fontSize = 9.sp,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f) else palette.textSecondary
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "مطلوب ${boardSize.requiredToWin} في خط واحد للفوز • مستوى: ${boardSize.difficultyLabelAr}"
                    else
                        "${boardSize.requiredToWin} in a line to win • Difficulty: ${boardSize.difficultyLabelEn}",
                    fontSize = 11.sp,
                    color = palette.textSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceVariant.copy(alpha = 0.4f))
                        .clickable { viewModel.toggleProgressiveScaling() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "تصعيد المربعات التلقائي مع كل فوز 🔥" else "Progressive Grid Scaling 🔥",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "يكبر حجم الشبكة تلقائياً بعد كل انتصار ضد الذكاء الاصطناعي" else "Increases squares automatically after beating AI",
                            fontSize = 10.sp,
                            color = palette.textSecondary
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = progressiveScalingEnabled,
                        onCheckedChange = { viewModel.toggleProgressiveScaling() },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = palette.accent,
                            checkedTrackColor = palette.surfaceVariant
                        )
                    )
                }
            }
        }

        // Turn Timer
        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "مؤقت النقلة (ثوانٍ)" else "Turn Timer (Seconds)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(null to if (lang == AppLanguage.ARABIC) "بدون" else "None", 5 to "5s ⚡", 10 to "10s").forEach { (sec, label) ->
                        val isSelected = maxTurnTime == sec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) palette.primaryO else palette.surfaceVariant)
                                .clickable { viewModel.startNewGame(timerSeconds = sec) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.White else palette.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section: Visual & Audio
        SettingsSectionTitle(title = if (lang == AppLanguage.ARABIC) "المظهر والصوت" else "Appearance & Audio", palette = palette)

        // Theme Selector
        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "سمة الألوان" else "Color Theme",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        GameThemeType.NEON_CYBER to "Neon",
                        GameThemeType.MINIMAL_DARK to "Slate",
                        GameThemeType.ARCADE_GOLD to "Gold",
                        GameThemeType.COSMIC_PURPLE to "Cosmic"
                    ).forEach { (th, name) ->
                        val isSelected = themeType == th
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) palette.accent else palette.surfaceVariant)
                                .clickable { viewModel.setTheme(th) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) androidx.compose.ui.graphics.Color.Black else palette.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Audio, Volume, Clips Studio & Haptics
        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Sound Toggle
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
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = null,
                            tint = if (soundEnabled) palette.accent else palette.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "المؤثرات الصوتية" else "Sound Effects",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "أصوات فريدة لكل حركة ولحظة فوز وهزيمة" else "Unique dynamic audio clips for moves, wins & losses",
                                fontSize = 11.sp,
                                color = palette.textSecondary
                            )
                        }
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.toggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = palette.accent),
                        modifier = Modifier.testTag("sound_effects_toggle")
                    )
                }

                // Volume Slider (only when sound is enabled)
                if (soundEnabled) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
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
                                    imageVector = when {
                                        soundVolume <= 0.01f -> Icons.Default.VolumeMute
                                        soundVolume < 0.5f -> Icons.Default.VolumeDown
                                        else -> Icons.Default.VolumeUp
                                    },
                                    contentDescription = null,
                                    tint = palette.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (lang == AppLanguage.ARABIC) "مستوى الصوت" else "Sound Volume",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.textPrimary
                                )
                            }
                            Text(
                                text = "${(soundVolume * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.accent
                            )
                        }

                        Slider(
                            value = soundVolume,
                            onValueChange = { viewModel.setSoundVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.accent,
                                activeTrackColor = palette.accent,
                                inactiveTrackColor = palette.border
                            ),
                            modifier = Modifier.testTag("sound_volume_slider")
                        )
                    }

                    // Sound FX Studio Preview
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "استوديو المؤثرات (استمع وتفاعل)" else "Sound FX Studio (Tap to Test)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                        }

                        // Grid of interactive sound cards
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GameSoundEffect.values().forEach { effect ->
                                val isSelected = lastPreviewedEffect == effect
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) palette.accent.copy(alpha = 0.15f)
                                            else palette.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) palette.accent else palette.border.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            lastPreviewedEffect = effect
                                            viewModel.previewSound(effect)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                        .testTag("sound_fx_${effect.name.lowercase()}"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = effect.icon, fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = if (lang == AppLanguage.ARABIC) effect.titleAr else effect.titleEn,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textPrimary
                                            )
                                            Text(
                                                text = if (lang == AppLanguage.ARABIC) effect.descriptionAr else effect.descriptionEn,
                                                fontSize = 10.sp,
                                                color = palette.textSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) palette.accent else palette.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play ${effect.name}",
                                            tint = if (isSelected) palette.background else palette.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Vibration Toggle
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
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = if (hapticsEnabled) palette.accent else palette.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "الاهتزاز والتفاعل اللمسي" else "Haptic Feedback",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = if (lang == AppLanguage.ARABIC) "نبضات لمسية حركية مرافقة للأصوات" else "Tactile vibration synchronized with audio cues",
                                fontSize = 11.sp,
                                color = palette.textSecondary
                            )
                        }
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.toggleHaptics() },
                        colors = SwitchDefaults.colors(checkedThumbColor = palette.accent),
                        modifier = Modifier.testTag("haptic_feedback_toggle")
                    )
                }
            }
        }

        // Section: Google Play Store & Legal
        SettingsSectionTitle(title = if (lang == AppLanguage.ARABIC) "معلومات Google Play والخصوصية" else "Google Play & About", palette = palette)

        SettingsCard(palette = palette) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Privacy Policy row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = Strings.privacyPolicy(lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.textPrimary
                        )
                    }
                    Text(text = "›", fontSize = 20.sp, color = palette.textSecondary)
                }

                // About row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAboutDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = palette.primaryX,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = Strings.aboutApp(lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.textPrimary
                        )
                    }
                    Text(text = "›", fontSize = 20.sp, color = palette.textSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = Strings.privacyPolicy(lang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            },
            text = {
                Text(
                    text = Strings.privacyPolicyText(lang),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = palette.textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "موافق" else "OK",
                        color = palette.accent
                    )
                }
            },
            containerColor = palette.surface
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = Strings.aboutApp(lang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            },
            text = {
                Text(
                    text = Strings.aboutAppText(lang),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = palette.textSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "إغلاق" else "Close",
                        color = palette.accent
                    )
                }
            },
            containerColor = palette.surface
        )
    }
}

@Composable
private fun SettingsSectionTitle(title: String, palette: com.example.ui.theme.GameThemePalette) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = palette.accent,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun SettingsCard(
    palette: com.example.ui.theme.GameThemePalette,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
