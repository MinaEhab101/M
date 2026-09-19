package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameSoundEffect
import com.example.audio.SoundManager
import com.example.data.auth.AuthManager
import com.example.data.local.AppDatabase
import com.example.data.local.MatchRecordEntity
import com.example.data.model.AIDifficulty
import com.example.data.model.Achievement
import com.example.data.model.AppLanguage
import com.example.data.model.BoardSize
import com.example.data.model.GameMode
import com.example.data.model.GameThemeType
import com.example.data.model.Player
import com.example.data.model.UserProfile
import com.example.data.model.WinningLine
import com.example.data.repository.GameRepository
import com.example.engine.TicTacToeEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = GameRepository(db.gameHistoryDao())
    val soundManager = SoundManager(application)
    val authManager = AuthManager(application)
    val firestoreManager = com.example.data.firestore.FirestoreManager(application)

    val historyRecords: Flow<List<MatchRecordEntity>> = repository.historyRecords
    val currentUser: StateFlow<UserProfile> = authManager.currentUser
    val authMessage: StateFlow<String?> = authManager.authMessage
    val firestoreSyncStatus = firestoreManager.syncStatus
    val firestoreSyncMessage = firestoreManager.syncMessage
    val globalLeaderboard = firestoreManager.observeGlobalLeaderboard()
    val topWinsLeaderboard = firestoreManager.observeTopWinsLeaderboard(10)

    // Settings State
    private val _themeType = MutableStateFlow(GameThemeType.NEON_CYBER)
    val themeType: StateFlow<GameThemeType> = _themeType.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ARABIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _soundVolume = MutableStateFlow(0.85f)
    val soundVolume: StateFlow<Float> = _soundVolume.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    // Game Configuration
    private val _gameMode = MutableStateFlow(GameMode.VS_AI)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _aiDifficulty = MutableStateFlow(AIDifficulty.MEDIUM)
    val aiDifficulty: StateFlow<AIDifficulty> = _aiDifficulty.asStateFlow()

    private val _boardSize = MutableStateFlow(BoardSize.SIZE_3X3)
    val boardSize: StateFlow<BoardSize> = _boardSize.asStateFlow()

    private val _progressiveScalingEnabled = MutableStateFlow(false)
    val progressiveScalingEnabled: StateFlow<Boolean> = _progressiveScalingEnabled.asStateFlow()

    private val _difficultyEscalationNotice = MutableStateFlow<String?>(null)
    val difficultyEscalationNotice: StateFlow<String?> = _difficultyEscalationNotice.asStateFlow()

    private val _maxTurnTimerSeconds = MutableStateFlow<Int?>(null)
    val maxTurnTimerSeconds: StateFlow<Int?> = _maxTurnTimerSeconds.asStateFlow()

    // Game Session State
    private val _board = MutableStateFlow<List<Player?>>(List(9) { null })
    val board: StateFlow<List<Player?>> = _board.asStateFlow()

    private val _currentTurn = MutableStateFlow(Player.X)
    val currentTurn: StateFlow<Player> = _currentTurn.asStateFlow()

    private val _winningLine = MutableStateFlow<WinningLine?>(null)
    val winningLine: StateFlow<WinningLine?> = _winningLine.asStateFlow()

    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _timeRemainingSeconds = MutableStateFlow<Int?>(null)
    val timeRemainingSeconds: StateFlow<Int?> = _timeRemainingSeconds.asStateFlow()

    private val _scoreX = MutableStateFlow(0)
    val scoreX: StateFlow<Int> = _scoreX.asStateFlow()

    private val _scoreO = MutableStateFlow(0)
    val scoreO: StateFlow<Int> = _scoreO.asStateFlow()

    private val _scoreDraw = MutableStateFlow(0)
    val scoreDraw: StateFlow<Int> = _scoreDraw.asStateFlow()

    private val _lastMoveIndex = MutableStateFlow<Int?>(null)
    val lastMoveIndex: StateFlow<Int?> = _lastMoveIndex.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _recentAchievementUnlock = MutableStateFlow<Achievement?>(null)
    val recentAchievementUnlock: StateFlow<Achievement?> = _recentAchievementUnlock.asStateFlow()

    private val _showDailyRewardDialog = MutableStateFlow(false)
    val showDailyRewardDialog: StateFlow<Boolean> = _showDailyRewardDialog.asStateFlow()

    private val _recentDailyRewardClaim = MutableStateFlow<com.example.data.model.DailyRewardClaimResult?>(null)
    val recentDailyRewardClaim: StateFlow<com.example.data.model.DailyRewardClaimResult?> = _recentDailyRewardClaim.asStateFlow()

    private var timerJob: Job? = null
    private var gameStartTime: Long = System.currentTimeMillis()

    init {
        soundManager.soundEnabled = _soundEnabled.value
        soundManager.hapticsEnabled = _hapticsEnabled.value
        soundManager.soundVolume = _soundVolume.value
    }

    fun startNewGame(
        mode: GameMode = _gameMode.value,
        difficulty: AIDifficulty = _aiDifficulty.value,
        size: BoardSize = _boardSize.value,
        timerSeconds: Int? = _maxTurnTimerSeconds.value
    ) {
        _gameMode.value = mode
        _aiDifficulty.value = difficulty
        _boardSize.value = size
        _maxTurnTimerSeconds.value = timerSeconds

        resetBoard()
    }

    fun resetBoard() {
        timerJob?.cancel()
        val totalCells = _boardSize.value.dimension * _boardSize.value.dimension
        _board.value = List(totalCells) { null }
        _currentTurn.value = Player.X
        _winningLine.value = null
        _winner.value = null
        _isGameOver.value = false
        _lastMoveIndex.value = null
        _movesCount.value = 0
        gameStartTime = System.currentTimeMillis()

        startTurnTimer()
        soundManager.playGameStart()
    }

    fun onCellClicked(index: Int) {
        if (_isGameOver.value) return
        val currentBoard = _board.value
        if (index < 0 || index >= currentBoard.size || currentBoard[index] != null) return

        // If playing vs AI and it's AI's turn, ignore human taps
        if (_gameMode.value == GameMode.VS_AI && _currentTurn.value == Player.O) return

        executeMove(index, _currentTurn.value)
    }

    private fun executeMove(index: Int, player: Player) {
        val newBoard = _board.value.toMutableList()
        newBoard[index] = player
        _board.value = newBoard
        _lastMoveIndex.value = index
        _movesCount.value += 1

        val isAiMove = (player == Player.O && _gameMode.value == GameMode.VS_AI)
        soundManager.playMove(isX = player == Player.X, isAi = isAiMove)

        val win = TicTacToeEngine.checkWin(newBoard, _boardSize.value)
        if (win != null) {
            handleGameOver(winner = player, winningLine = win)
            return
        }

        if (TicTacToeEngine.isBoardFull(newBoard)) {
            handleGameOver(winner = null, winningLine = null)
            return
        }

        // Switch turn
        val nextTurn = player.opponent()
        _currentTurn.value = nextTurn
        startTurnTimer()

        // If vs AI and next turn is O, trigger AI move after a brief realistic thinking delay
        if (_gameMode.value == GameMode.VS_AI && nextTurn == Player.O) {
            viewModelScope.launch {
                delay(400) // Realistic feeling AI response time
                if (!_isGameOver.value) {
                    val aiMove = TicTacToeEngine.computeAIMove(
                        board = _board.value,
                        boardSize = _boardSize.value,
                        aiPlayer = Player.O,
                        difficulty = _aiDifficulty.value
                    )
                    if (aiMove != -1 && !_isGameOver.value) {
                        executeMove(aiMove, Player.O)
                    }
                }
            }
        }
    }

    private fun handleGameOver(winner: Player?, winningLine: WinningLine?) {
        timerJob?.cancel()
        _winningLine.value = winningLine
        _winner.value = winner
        _isGameOver.value = true

        val durationSec = ((System.currentTimeMillis() - gameStartTime) / 1000).toInt().coerceAtLeast(1)

        when (winner) {
            Player.X -> {
                _scoreX.value += 1
                soundManager.playWin()
            }
            Player.O -> {
                _scoreO.value += 1
                if (_gameMode.value == GameMode.VS_AI) {
                    soundManager.playLoss()
                } else {
                    soundManager.playWin()
                }
            }
            null -> {
                _scoreDraw.value += 1
                soundManager.playDraw()
            }
        }

        // Record XP, stats, and evaluate achievements in AuthManager
        val newlyUnlocked = authManager.recordGameOutcome(
            winner = winner,
            isAi = _gameMode.value == GameMode.VS_AI,
            difficulty = if (_gameMode.value == GameMode.VS_AI) _aiDifficulty.value else null,
            boardDimension = _boardSize.value.dimension,
            durationSec = durationSec
        )

        if (newlyUnlocked.isNotEmpty()) {
            _recentAchievementUnlock.value = newlyUnlocked.first()
            soundManager.playAchievementUnlocked()
            for (ach in newlyUnlocked) {
                firestoreManager.syncAchievementUnlocked(currentUser.value.id, ach)
            }
        }

        // Check progressive difficulty scaling
        if (_progressiveScalingEnabled.value && winner == Player.X && _gameMode.value == GameMode.VS_AI) {
            val nextSize = _boardSize.value.nextBiggerSize()
            if (nextSize != null) {
                _boardSize.value = nextSize
                _difficultyEscalationNotice.value = if (_language.value == AppLanguage.ARABIC)
                    "🔥 فوز رائع! تم تصعيد صعوبة اللعبة إلى ${nextSize.titleAr} (${nextSize.difficultyLabelAr})"
                else
                    "🔥 Great win! Difficulty escalated to ${nextSize.titleEn} (${nextSize.difficultyLabelEn})"
            } else {
                _difficultyEscalationNotice.value = if (_language.value == AppLanguage.ARABIC)
                    "👑 إنجاز أسطوري! لقد تغلبت على التحدي الأقصى 7×7 (49 مربع)!"
                else
                    "👑 Legendary achievement! You reached the peak 7x7 (49 squares) challenge!"
            }
        }

        // Save match record in Room database and sync to Cloud Firestore
        viewModelScope.launch {
            val xpGained = when {
                winner == Player.X -> 30
                winner == null -> 10
                else -> 5
            }
            val matchId = repository.saveMatch(
                mode = _gameMode.value.name,
                aiDifficulty = if (_gameMode.value == GameMode.VS_AI) _aiDifficulty.value.name else null,
                winner = winner?.name ?: "DRAW",
                boardDimension = _boardSize.value.dimension,
                movesCount = _movesCount.value,
                durationSeconds = durationSec,
                xpEarned = xpGained
            )

            val matchEntity = MatchRecordEntity(
                id = matchId,
                timestamp = System.currentTimeMillis(),
                mode = _gameMode.value.name,
                aiDifficulty = if (_gameMode.value == GameMode.VS_AI) _aiDifficulty.value.name else null,
                winner = winner?.name ?: "DRAW",
                boardDimension = _boardSize.value.dimension,
                movesCount = _movesCount.value,
                durationSeconds = durationSec,
                xpEarned = xpGained
            )

            firestoreManager.syncMatchRecord(currentUser.value.id, matchEntity)
            firestoreManager.syncUserProfile(currentUser.value)
        }
    }

    private fun startTurnTimer() {
        timerJob?.cancel()
        val maxTime = _maxTurnTimerSeconds.value
        if (maxTime == null || maxTime <= 0) {
            _timeRemainingSeconds.value = null
            return
        }

        _timeRemainingSeconds.value = maxTime
        timerJob = viewModelScope.launch {
            var timeLeft = maxTime
            while (timeLeft > 0 && !_isGameOver.value) {
                if (timeLeft in 1..3) {
                    soundManager.playTimerWarning()
                }
                delay(1000)
                timeLeft -= 1
                _timeRemainingSeconds.value = timeLeft
            }
            if (timeLeft <= 0 && !_isGameOver.value) {
                // Time's up: forfeit turn to opponent
                val timeoutWinner = _currentTurn.value.opponent()
                handleGameOver(winner = timeoutWinner, winningLine = null)
            }
        }
    }

    fun undoLastMove() {
        if (_isGameOver.value) return
        val currentBoard = _board.value.toMutableList()

        if (_gameMode.value == GameMode.VS_AI) {
            // Need to undo both AI and player moves if it's player's turn
            val emptyCount = currentBoard.count { it == null }
            if (emptyCount <= currentBoard.size - 2) {
                // Find last two non-null and clear
                val indices = currentBoard.indices.filter { currentBoard[it] != null }
                if (indices.size >= 2) {
                    currentBoard[indices.last()] = null
                    currentBoard[indices[indices.size - 2]] = null
                    _board.value = currentBoard
                    _currentTurn.value = Player.X
                    _movesCount.value = maxOf(0, _movesCount.value - 2)
                    startTurnTimer()
                    soundManager.playUndo()
                }
            }
        } else {
            val lastIdx = _lastMoveIndex.value
            if (lastIdx != null && currentBoard[lastIdx] != null) {
                currentBoard[lastIdx] = null
                _board.value = currentBoard
                _currentTurn.value = _currentTurn.value.opponent()
                _movesCount.value = maxOf(0, _movesCount.value - 1)
                startTurnTimer()
                soundManager.playUndo()
            }
        }
    }

    fun setTheme(theme: GameThemeType) {
        _themeType.value = theme
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleSound() {
        val newVal = !_soundEnabled.value
        _soundEnabled.value = newVal
        soundManager.soundEnabled = newVal
    }

    fun setSoundVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _soundVolume.value = clamped
        soundManager.soundVolume = clamped
        if (clamped > 0f && !_soundEnabled.value) {
            _soundEnabled.value = true
            soundManager.soundEnabled = true
        }
    }

    fun previewSound(effect: GameSoundEffect) {
        soundManager.playEffect(effect)
    }

    fun toggleHaptics() {
        val newVal = !_hapticsEnabled.value
        _hapticsEnabled.value = newVal
        soundManager.hapticsEnabled = newVal
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(activity)
            if (result.isSuccess) {
                val user = currentUser.value
                val remoteProfile = firestoreManager.fetchRemoteUserProfile(user.id)
                if (remoteProfile != null) {
                    val mergedAchievements = (user.unlockedAchievements + remoteProfile.unlockedAchievements).distinct()
                    val mergedUser = user.copy(
                        xp = maxOf(user.xp, remoteProfile.xp),
                        level = maxOf(user.level, remoteProfile.level),
                        wins = maxOf(user.wins, remoteProfile.wins),
                        losses = maxOf(user.losses, remoteProfile.losses),
                        draws = maxOf(user.draws, remoteProfile.draws),
                        totalGames = maxOf(user.totalGames, remoteProfile.totalGames),
                        currentStreak = maxOf(user.currentStreak, remoteProfile.currentStreak),
                        bestStreak = maxOf(user.bestStreak, remoteProfile.bestStreak),
                        unlockedAchievements = mergedAchievements
                    )
                    authManager.updateProfile(mergedUser)
                    firestoreManager.syncUserProfile(mergedUser)
                } else {
                    firestoreManager.syncUserProfile(user)
                }
            }
        }
    }

    fun dismissAchievementUnlock() {
        _recentAchievementUnlock.value = null
    }

    fun syncNowWithCloud() {
        firestoreManager.syncUserProfile(currentUser.value)
    }

    fun clearFirestoreMessage() {
        firestoreManager.clearSyncMessage()
    }

    fun signOut() {
        authManager.signOut()
    }

    fun updateDisplayName(name: String) {
        authManager.updateDisplayName(name)
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun clearAuthMessage() {
        authManager.clearAuthMessage()
    }

    fun setBoardSize(size: BoardSize) {
        _boardSize.value = size
        resetBoard()
    }

    fun increaseBoardSquares() {
        val next = _boardSize.value.nextBiggerSize()
        if (next != null) {
            setBoardSize(next)
        }
    }

    fun decreaseBoardSquares() {
        val prev = _boardSize.value.nextSmallerSize()
        if (prev != null) {
            setBoardSize(prev)
        }
    }

    fun toggleProgressiveScaling() {
        _progressiveScalingEnabled.value = !_progressiveScalingEnabled.value
    }

    fun clearEscalationNotice() {
        _difficultyEscalationNotice.value = null
    }

    fun openDailyRewardDialog() {
        _showDailyRewardDialog.value = true
    }

    fun dismissDailyRewardDialog() {
        _showDailyRewardDialog.value = false
        _recentDailyRewardClaim.value = null
    }

    fun claimDailyReward(): com.example.data.model.DailyRewardClaimResult? {
        val result = authManager.claimDailyReward() ?: return null
        _recentDailyRewardClaim.value = result
        soundManager.playRewardClaim()

        val profile = currentUser.value
        // Immediately sync complete updated profile to Cloud Firestore
        firestoreManager.syncUserProfile(profile)

        // Also write atomic receipt record into Firestore users/{userId}/daily_rewards subcollection
        firestoreManager.syncDailyRewardClaimed(
            userId = profile.id,
            day = result.dayNumber,
            coins = result.coinsEarned,
            xp = result.xpEarned,
            streak = result.loginStreak
        )

        return result
    }
}
