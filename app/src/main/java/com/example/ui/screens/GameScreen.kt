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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.GameMode
import com.example.data.model.Player
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GameOverDialog
import com.example.ui.components.ScoreHeader
import com.example.ui.components.XOBoard
import com.example.ui.components.BoardSquareController
import com.example.ui.localization.Strings
import com.example.ui.theme.GameColors
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val themeType by viewModel.themeType.collectAsState()
    val palette = GameColors.getPalette(themeType)
    val user by viewModel.currentUser.collectAsState()

    val board by viewModel.board.collectAsState()
    val boardSize by viewModel.boardSize.collectAsState()
    val currentTurn by viewModel.currentTurn.collectAsState()
    val winningLine by viewModel.winningLine.collectAsState()
    val winner by viewModel.winner.collectAsState()
    val isGameOver by viewModel.isGameOver.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()
    val difficulty by viewModel.aiDifficulty.collectAsState()
    val timeRemaining by viewModel.timeRemainingSeconds.collectAsState()
    val maxTurnTime by viewModel.maxTurnTimerSeconds.collectAsState()
    val scoreX by viewModel.scoreX.collectAsState()
    val scoreO by viewModel.scoreO.collectAsState()
    val scoreDraw by viewModel.scoreDraw.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val movesCount by viewModel.movesCount.collectAsState()
    val progressiveScalingEnabled by viewModel.progressiveScalingEnabled.collectAsState()
    val escalationNotice by viewModel.difficultyEscalationNotice.collectAsState()
    val recentAchievement by viewModel.recentAchievementUnlock.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.textPrimary
                    )
                }

                // Mode Title Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceVariant)
                        .border(1.dp, palette.gridLine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (gameMode == GameMode.VS_AI) Strings.playVsAI(lang) else Strings.twoPlayers(lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleSound() },
                        modifier = Modifier
                            .size(40.dp)
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
                        onClick = { viewModel.resetBoard() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(palette.surfaceVariant)
                            .testTag("restart_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = palette.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Score Header
            ScoreHeader(
                playerXName = user.displayName,
                playerOName = if (gameMode == GameMode.VS_AI) "الذكاء الاصطناعي" else "اللاعب (O)",
                currentTurn = currentTurn,
                mode = gameMode,
                difficulty = difficulty,
                scoreX = scoreX,
                scoreDraw = scoreDraw,
                scoreO = scoreO,
                timeRemainingSeconds = timeRemaining,
                maxTurnTimeSeconds = maxTurnTime,
                lang = lang,
                palette = palette
            )

            // Turn Status Banner
            val turnText = if (isGameOver) {
                when (winner) {
                    Player.X -> Strings.xWins(lang)
                    Player.O -> Strings.oWins(lang, gameMode)
                    null -> Strings.drawGame(lang)
                }
            } else {
                Strings.turnText(lang, isX = currentTurn == Player.X, mode = gameMode)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = turnText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentTurn == Player.X) palette.primaryX else palette.primaryO,
                    textAlign = TextAlign.Center
                )
            }

            // The Interactive Game Board
            XOBoard(
                board = board,
                boardSize = boardSize,
                winningLine = winningLine,
                palette = palette,
                enabled = !isGameOver,
                onCellClick = { index -> viewModel.onCellClicked(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom Action Controls: Undo & Restart Match
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (gameMode == GameMode.VS_AI) {
                    OutlinedButton(
                        onClick = { viewModel.undoLastMove() },
                        enabled = movesCount >= 2 && !isGameOver,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("undo_move_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "تراجع" else "Undo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.textPrimary
                        )
                    }
                }

                Button(
                    onClick = { viewModel.resetBoard() },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Match",
                        tint = palette.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "جولة جديدة" else "New Round",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }
            }

            // Interactive Board Squares & Progressive Difficulty Controller
            BoardSquareController(
                currentSize = boardSize,
                progressiveScalingEnabled = progressiveScalingEnabled,
                onSizeSelected = { newSize -> viewModel.setBoardSize(newSize) },
                onIncreaseSquares = { viewModel.increaseBoardSquares() },
                onDecreaseSquares = { viewModel.decreaseBoardSquares() },
                onToggleProgressiveScaling = { viewModel.toggleProgressiveScaling() },
                lang = lang,
                palette = palette,
                escalationNotice = escalationNotice,
                onDismissEscalationNotice = { viewModel.clearEscalationNotice() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Win Celebration Confetti
        if (isGameOver && winner != null) {
            ConfettiEffect()
        }

        // Match Over Dialog
        if (isGameOver) {
            val xpEarned = when {
                winner == Player.X -> 30
                winner == null -> 10
                else -> 5
            }
            GameOverDialog(
                winner = winner,
                mode = gameMode,
                xpEarned = xpEarned,
                lang = lang,
                palette = palette,
                recentAchievement = recentAchievement,
                onPlayAgain = {
                    viewModel.dismissAchievementUnlock()
                    viewModel.resetBoard()
                },
                onMainMenu = {
                    viewModel.dismissAchievementUnlock()
                    onBackToHome()
                }
            )
        }
    }
}
