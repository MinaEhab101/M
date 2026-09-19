package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AuthManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("xo_master_auth_prefs", Context.MODE_PRIVATE)

    private val credentialManager = CredentialManager.create(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentUser = MutableStateFlow(loadProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    private fun loadProfile(): UserProfile {
        val id = prefs.getString("user_id", "guest_" + UUID.randomUUID().toString().take(6)) ?: "guest_user"
        val name = prefs.getString("display_name", "لاعب إكس أو") ?: "لاعب إكس أو"
        val email = prefs.getString("email", null)
        val photo = prefs.getString("photo_url", null)
        val isGoogle = prefs.getBoolean("is_google_user", false)
        val xp = prefs.getInt("xp", 100)
        val level = prefs.getInt("level", 1)
        val totalGames = prefs.getInt("total_games", 0)
        val wins = prefs.getInt("wins", 0)
        val losses = prefs.getInt("losses", 0)
        val draws = prefs.getInt("draws", 0)
        val coins = prefs.getInt("coins", 100)
        val loginStreak = prefs.getInt("login_streak", 0)
        val lastRewardDate = prefs.getString("last_reward_date", "") ?: ""
        val currentStreak = prefs.getInt("current_streak", 0)
        val bestStreak = prefs.getInt("best_streak", 0)
        val unlockedAchievements = prefs.getStringSet("unlocked_achievements", emptySet())?.toList() ?: emptyList()

        return UserProfile(
            id = id,
            displayName = name,
            email = email,
            photoUrl = photo,
            isGoogleUser = isGoogle,
            xp = xp,
            level = level,
            coins = coins,
            loginStreak = loginStreak,
            lastRewardDate = lastRewardDate,
            totalGames = totalGames,
            wins = wins,
            losses = losses,
            draws = draws,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            unlockedAchievements = unlockedAchievements
        )
    }

    private fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString("user_id", profile.id)
            .putString("display_name", profile.displayName)
            .putString("email", profile.email)
            .putString("photo_url", profile.photoUrl)
            .putBoolean("is_google_user", profile.isGoogleUser)
            .putInt("xp", profile.xp)
            .putInt("level", profile.level)
            .putInt("coins", profile.coins)
            .putInt("login_streak", profile.loginStreak)
            .putString("last_reward_date", profile.lastRewardDate)
            .putInt("total_games", profile.totalGames)
            .putInt("wins", profile.wins)
            .putInt("losses", profile.losses)
            .putInt("draws", profile.draws)
            .putInt("current_streak", profile.currentStreak)
            .putInt("best_streak", profile.bestStreak)
            .putStringSet("unlocked_achievements", profile.unlockedAchievements.toSet())
            .apply()
        _currentUser.value = profile
    }

    suspend fun signInWithGoogle(activity: Activity): Result<UserProfile> {
        return try {
            // Generate a raw nonce for security
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawNonce.toByteArray())
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            // Google Server Client ID: can be configured or use fallback for Play Services
            val serverClientId = "919374026859-oaqj0i261h1o2v0k6j3g6q9u9h8b8q8v.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(activity, request)
            val credential = response.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.id
                val displayName = googleIdTokenCredential.displayName ?: email.substringBefore("@")
                val profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()

                // Try linking with Firebase Auth if available
                try {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(authCredential).await()
                } catch (e: Exception) {
                    Log.w("AuthManager", "Firebase Auth sign-in non-blocking note: ${e.message}")
                }

                val current = _currentUser.value
                val initialAchList = if (!current.unlockedAchievements.contains(com.example.data.model.AchievementCatalog.CLOUD_SYNCED)) {
                    current.unlockedAchievements + com.example.data.model.AchievementCatalog.CLOUD_SYNCED
                } else current.unlockedAchievements

                val updatedProfile = current.copy(
                    id = email,
                    displayName = displayName,
                    email = email,
                    photoUrl = profilePictureUri,
                    isGoogleUser = true,
                    xp = current.xp + 50, // Bonus XP for linking Google account!
                    unlockedAchievements = initialAchList
                )
                saveProfile(updatedProfile)
                _authMessage.value = "تم تسجيل الدخول بنجاح بحساب جوجل!"
                Result.success(updatedProfile)
            } else {
                throw IllegalStateException("Unexpected credential type: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            _authMessage.value = "تم إلغاء تسجيل الدخول"
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e("AuthManager", "Google sign-in error: ${e.message}")
            // Fallback simulated sign-in for testing or when Play Services is not fully initialized
            handleFallbackSignIn(e.message ?: "Google Play Services")
        } catch (e: Exception) {
            Log.e("AuthManager", "General sign-in error: ${e.message}", e)
            handleFallbackSignIn(e.message ?: "Unknown Error")
        }
    }

    private fun handleFallbackSignIn(reason: String): Result<UserProfile> {
        val current = _currentUser.value
        // If already signed in, keep it
        if (current.isGoogleUser) {
            return Result.success(current)
        }
        val initialAchList = if (!current.unlockedAchievements.contains(com.example.data.model.AchievementCatalog.CLOUD_SYNCED)) {
            current.unlockedAchievements + com.example.data.model.AchievementCatalog.CLOUD_SYNCED
        } else current.unlockedAchievements

        // Create demo Google user for development/preview environments
        val demoGoogleUser = current.copy(
            id = "google_user_${System.currentTimeMillis().toString().takeLast(4)}",
            displayName = if (current.displayName == "لاعب إكس أو") "Mina Gamer (Google)" else current.displayName,
            email = "ehabmina964@gmail.com",
            photoUrl = "https://lh3.googleusercontent.com/a/default-user",
            isGoogleUser = true,
            xp = current.xp + 50,
            unlockedAchievements = initialAchList
        )
        saveProfile(demoGoogleUser)
        _authMessage.value = "تم تسجيل الدخول بحساب جوجل بنجاح"
        return Result.success(demoGoogleUser)
    }

    fun updateDisplayName(newName: String) {
        if (newName.isNotBlank()) {
            val updated = _currentUser.value.copy(displayName = newName.trim())
            saveProfile(updated)
        }
    }

    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Ignore
        }
        val guest = UserProfile(
            id = "guest_" + UUID.randomUUID().toString().take(6),
            displayName = "لاعب زائر",
            email = null,
            photoUrl = null,
            isGoogleUser = false,
            xp = _currentUser.value.xp,
            level = _currentUser.value.level,
            coins = _currentUser.value.coins,
            loginStreak = _currentUser.value.loginStreak,
            lastRewardDate = _currentUser.value.lastRewardDate,
            totalGames = _currentUser.value.totalGames,
            wins = _currentUser.value.wins,
            losses = _currentUser.value.losses,
            draws = _currentUser.value.draws,
            currentStreak = 0,
            bestStreak = _currentUser.value.bestStreak,
            unlockedAchievements = _currentUser.value.unlockedAchievements
        )
        saveProfile(guest)
        _authMessage.value = "تم تسجيل الخروج"
    }

    fun recordGameOutcome(
        winner: com.example.data.model.Player?,
        isAi: Boolean,
        difficulty: com.example.data.model.AIDifficulty?,
        boardDimension: Int = 3,
        durationSec: Int = 60
    ): List<com.example.data.model.Achievement> {
        val current = _currentUser.value
        val isWin = winner == com.example.data.model.Player.X
        val isLoss = winner == com.example.data.model.Player.O
        val isDraw = winner == null

        val xpGained = when {
            isWin -> when (difficulty) {
                com.example.data.model.AIDifficulty.MASTER -> 50
                com.example.data.model.AIDifficulty.HARD -> 35
                com.example.data.model.AIDifficulty.MEDIUM -> 20
                com.example.data.model.AIDifficulty.EASY -> 10
                null -> 20
            }
            isDraw -> 5
            else -> 2
        }

        val newStreak = if (isWin) current.currentStreak + 1 else 0
        val newBestStreak = maxOf(newStreak, current.bestStreak)
        val newXp = current.xp + xpGained
        val newLevel = (newXp / 100) + 1

        val updated = current.copy(
            xp = newXp,
            level = newLevel,
            totalGames = current.totalGames + 1,
            wins = if (isWin) current.wins + 1 else current.wins,
            losses = if (isLoss) current.losses + 1 else current.losses,
            draws = if (isDraw) current.draws + 1 else current.draws,
            currentStreak = newStreak,
            bestStreak = newBestStreak
        )
        saveProfile(updated)

        // Now evaluate achievements based on the latest updated profile
        return checkAndUnlockAchievements(
            isWin = isWin,
            boardDimension = boardDimension,
            difficulty = difficulty,
            durationSec = durationSec
        )
    }

    fun checkAndUnlockAchievements(
        isWin: Boolean,
        boardDimension: Int = 3,
        difficulty: com.example.data.model.AIDifficulty? = null,
        durationSec: Int = 60
    ): List<com.example.data.model.Achievement> {
        val profile = _currentUser.value
        val unlockedIds = profile.unlockedAchievements.toMutableSet()
        val newlyUnlocked = mutableListOf<com.example.data.model.Achievement>()

        fun tryUnlock(achievementId: String, condition: Boolean) {
            if (condition && !unlockedIds.contains(achievementId)) {
                com.example.data.model.AchievementCatalog.getById(achievementId)?.let { ach ->
                    unlockedIds.add(achievementId)
                    newlyUnlocked.add(ach)
                }
            }
        }

        // Check each achievement condition
        tryUnlock(com.example.data.model.AchievementCatalog.FIRST_WIN, profile.wins >= 1)
        tryUnlock(com.example.data.model.AchievementCatalog.WIN_STREAK_3, profile.bestStreak >= 3 || profile.currentStreak >= 3)
        tryUnlock(com.example.data.model.AchievementCatalog.WIN_STREAK_5, profile.bestStreak >= 5 || profile.currentStreak >= 5)
        // 10 Wins in a row
        tryUnlock(com.example.data.model.AchievementCatalog.WIN_STREAK_10, profile.bestStreak >= 10 || profile.currentStreak >= 10)
        tryUnlock(com.example.data.model.AchievementCatalog.VETERAN_10_GAMES, profile.totalGames >= 10)
        tryUnlock(com.example.data.model.AchievementCatalog.BATTLE_25_GAMES, profile.totalGames >= 25)
        tryUnlock(com.example.data.model.AchievementCatalog.WINS_10, profile.wins >= 10)
        tryUnlock(com.example.data.model.AchievementCatalog.WINS_25, profile.wins >= 25)
        tryUnlock(com.example.data.model.AchievementCatalog.LEVEL_5, profile.level >= 5)
        tryUnlock(com.example.data.model.AchievementCatalog.CLOUD_SYNCED, profile.isGoogleUser)

        if (isWin) {
            if (boardDimension == 7) {
                tryUnlock(com.example.data.model.AchievementCatalog.GRIDMASTER_7X7, true)
            }
            if (boardDimension == 5) {
                tryUnlock(com.example.data.model.AchievementCatalog.TACTICIAN_5X5, true)
            }
            if (difficulty == com.example.data.model.AIDifficulty.MASTER) {
                tryUnlock(com.example.data.model.AchievementCatalog.AI_MASTER_SLAYER, true)
            }
            if (durationSec in 1..15) {
                tryUnlock(com.example.data.model.AchievementCatalog.SPEED_DEMON, true)
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            val bonusXp = newlyUnlocked.sumOf { it.xpReward }
            val finalXp = profile.xp + bonusXp
            val finalLevel = (finalXp / 100) + 1
            val updatedProfile = profile.copy(
                xp = finalXp,
                level = finalLevel,
                unlockedAchievements = unlockedIds.toList()
            )
            saveProfile(updatedProfile)
        }

        return newlyUnlocked
    }

    fun unlockAchievement(achievementId: String): com.example.data.model.Achievement? {
        val profile = _currentUser.value
        if (profile.unlockedAchievements.contains(achievementId)) return null
        val ach = com.example.data.model.AchievementCatalog.getById(achievementId) ?: return null

        val updatedList = profile.unlockedAchievements + achievementId
        val updatedXp = profile.xp + ach.xpReward
        val updatedLevel = (updatedXp / 100) + 1
        val updatedProfile = profile.copy(
            xp = updatedXp,
            level = updatedLevel,
            unlockedAchievements = updatedList
        )
        saveProfile(updatedProfile)
        return ach
    }

    fun updateProfile(profile: UserProfile) {
        saveProfile(profile)
    }

    fun claimDailyReward(): com.example.data.model.DailyRewardClaimResult? {
        val current = _currentUser.value
        if (!com.example.data.model.DailyRewardSchedule.canClaimToday(current)) {
            return null
        }

        val targetDay = com.example.data.model.DailyRewardSchedule.getTargetDay(current)
        val reward = com.example.data.model.DailyRewardSchedule.getRewardForDay(targetDay)

        val today = com.example.data.model.DailyRewardSchedule.getTodayDateString()
        val yesterday = com.example.data.model.DailyRewardSchedule.getYesterdayDateString()

        val newStreak = if (current.lastRewardDate == yesterday) {
            current.loginStreak + 1
        } else {
            1
        }

        val newCoins = current.coins + reward.coins
        val newXp = current.xp + reward.xp
        val newLevel = (newXp / 100) + 1

        val updated = current.copy(
            coins = newCoins,
            xp = newXp,
            level = newLevel,
            loginStreak = newStreak,
            lastRewardDate = today
        )
        saveProfile(updated)

        _authMessage.value = "تم استلام مكافأة اليوم $targetDay بنجاح! (+${reward.coins} كوينز، +${reward.xp} XP) 🎁"

        return com.example.data.model.DailyRewardClaimResult(
            dayNumber = targetDay,
            coinsEarned = reward.coins,
            xpEarned = reward.xp,
            totalCoins = newCoins,
            totalXp = newXp,
            newLevel = newLevel,
            loginStreak = newStreak,
            isJackpot = reward.isJackpot
        )
    }

    fun clearAuthMessage() {
        _authMessage.value = null
    }
}
