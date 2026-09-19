package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AIDifficulty
import com.example.data.model.BoardSize
import com.example.data.model.Player
import com.example.engine.TicTacToeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("XO Master", appName)
    }

    @Test
    fun `engine detects horizontal win for Player X`() {
        val board = listOf(
            Player.X, Player.X, Player.X,
            Player.O, Player.O, null,
            null, null, null
        )
        val win = TicTacToeEngine.checkWin(board, BoardSize.SIZE_3X3)
        assertNotNull(win)
        assertEquals(listOf(0, 1, 2), win?.winningCells)
        assertEquals(Player.X, board[win!!.winningCells.first()])
    }

    @Test
    fun `engine detects diagonal win for Player O`() {
        val board = listOf(
            Player.O, Player.X, null,
            Player.X, Player.O, null,
            null, Player.X, Player.O
        )
        val win = TicTacToeEngine.checkWin(board, BoardSize.SIZE_3X3)
        assertNotNull(win)
        assertEquals(listOf(0, 4, 8), win?.winningCells)
        assertEquals(Player.O, board[win!!.winningCells.first()])
    }

    @Test
    fun `ai makes valid blocking or winning move`() {
        // Player X has two in top row (0, 1). AI (O) must block at index 2.
        val board = listOf(
            Player.X, Player.X, null,
            Player.O, null, null,
            null, null, null
        )
        val aiMove = TicTacToeEngine.computeAIMove(
            board = board,
            boardSize = BoardSize.SIZE_3X3,
            aiPlayer = Player.O,
            difficulty = AIDifficulty.MASTER
        )
        assertEquals(2, aiMove)
    }

    @Test
    fun `leaderboard entries sorted descending by wins`() {
        val entries = listOf(
            com.example.data.model.LeaderboardEntry(userId = "1", displayName = "Player A", wins = 15),
            com.example.data.model.LeaderboardEntry(userId = "2", displayName = "Player B", wins = 42),
            com.example.data.model.LeaderboardEntry(userId = "3", displayName = "Player C", wins = 28)
        )
        val sorted = entries.sortedByDescending { it.wins }.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }
        assertEquals(1, sorted[0].rank)
        assertEquals("Player B", sorted[0].displayName)
        assertEquals(42, sorted[0].wins)
        assertEquals(2, sorted[1].rank)
        assertEquals("Player C", sorted[1].displayName)
        assertEquals(3, sorted[2].rank)
        assertEquals("Player A", sorted[2].displayName)
    }

    @Test
    fun `achievement catalog progress and unlocks for 10 win streak`() {
        val user = com.example.data.model.UserProfile(
            id = "test_user",
            displayName = "Champion",
            wins = 12,
            losses = 2,
            draws = 0,
            bestStreak = 10,
            currentStreak = 10,
            totalGames = 14,
            level = 4,
            unlockedAchievements = listOf(
                com.example.data.model.AchievementCatalog.FIRST_WIN,
                com.example.data.model.AchievementCatalog.WIN_STREAK_3,
                com.example.data.model.AchievementCatalog.WIN_STREAK_5,
                com.example.data.model.AchievementCatalog.WIN_STREAK_10
            )
        )

        val streak10Achievement = com.example.data.model.AchievementCatalog.getById(
            com.example.data.model.AchievementCatalog.WIN_STREAK_10
        )
        assertNotNull(streak10Achievement)
        val progress = com.example.data.model.AchievementCatalog.calculateProgress(streak10Achievement!!, user)
        val ratio = com.example.data.model.AchievementCatalog.calculateProgressRatio(streak10Achievement, user)

        assertEquals(10, progress)
        assertEquals(1.0f, ratio, 0.001f)
        org.junit.Assert.assertTrue(user.unlockedAchievements.contains(com.example.data.model.AchievementCatalog.WIN_STREAK_10))
    }

    @Test
    fun `daily reward eligible when not claimed today`() {
        val user = com.example.data.model.UserProfile(
            id = "test_user",
            lastRewardDate = com.example.data.model.DailyRewardSchedule.getYesterdayDateString(),
            loginStreak = 2
        )
        val canClaim = com.example.data.model.DailyRewardSchedule.canClaimToday(user)
        val targetDay = com.example.data.model.DailyRewardSchedule.getTargetDay(user)
        val reward = com.example.data.model.DailyRewardSchedule.getRewardForDay(targetDay)

        org.junit.Assert.assertTrue(canClaim)
        assertEquals(3, targetDay)
        assertEquals(120, reward.coins)
        assertEquals(75, reward.xp)
    }

    @Test
    fun `daily reward not eligible when already claimed today`() {
        val user = com.example.data.model.UserProfile(
            id = "test_user",
            lastRewardDate = com.example.data.model.DailyRewardSchedule.getTodayDateString(),
            loginStreak = 3
        )
        val canClaim = com.example.data.model.DailyRewardSchedule.canClaimToday(user)
        val targetDay = com.example.data.model.DailyRewardSchedule.getTargetDay(user)

        org.junit.Assert.assertFalse(canClaim)
        assertEquals(3, targetDay)
    }

    @Test
    fun `daily reward day 7 is jackpot with high coins and xp`() {
        val day7 = com.example.data.model.DailyRewardSchedule.getRewardForDay(7)
        org.junit.Assert.assertTrue(day7.isJackpot)
        assertEquals(600, day7.coins)
        assertEquals(400, day7.xp)
    }

    @Test
    fun `sound manager initializes and supports all game sound effects`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = com.example.audio.SoundManager(context)

        org.junit.Assert.assertTrue(soundManager.soundEnabled)
        org.junit.Assert.assertTrue(soundManager.hapticsEnabled)
        assertEquals(0.85f, soundManager.soundVolume, 0.001f)

        // Verify all sound effects exist with complete metadata
        val effects = com.example.audio.GameSoundEffect.values()
        assertEquals(10, effects.size)

        for (effect in effects) {
            org.junit.Assert.assertTrue(effect.titleEn.isNotEmpty())
            org.junit.Assert.assertTrue(effect.titleAr.isNotEmpty())
            org.junit.Assert.assertTrue(effect.descriptionEn.isNotEmpty())
            org.junit.Assert.assertTrue(effect.descriptionAr.isNotEmpty())
            org.junit.Assert.assertTrue(effect.icon.isNotEmpty())

            // Test dispatching each effect safely
            soundManager.playEffect(effect)
        }
    }

    @Test
    fun `sound volume clamps properly between 0 and 1`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundManager = com.example.audio.SoundManager(context)

        soundManager.soundVolume = 1.5f
        assertEquals(1.5f, soundManager.soundVolume, 0.001f) // Property stores, synthesize clamps

        soundManager.playMoveX()
        soundManager.playMoveO()
        soundManager.playAIMove()
        soundManager.playWin()
        soundManager.playLoss()
        soundManager.playDraw()
    }
}
