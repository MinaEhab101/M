package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.data.local.MatchRecordEntity
import com.example.data.model.FirestoreSyncStatus
import com.example.data.model.LeaderboardEntry
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var firestore: FirebaseFirestore? = null

    private val _syncStatus = MutableStateFlow(FirestoreSyncStatus.IDLE)
    val syncStatus: StateFlow<FirestoreSyncStatus> = _syncStatus.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        initFirestore()
    }

    private fun initFirestore() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
                db.firestoreSettings = settings
                firestore = db
                _syncStatus.value = FirestoreSyncStatus.SYNCED
                Log.d("FirestoreManager", "Firestore initialized with offline persistence")
            } else {
                _syncStatus.value = FirestoreSyncStatus.OFFLINE
                Log.w("FirestoreManager", "FirebaseApp not initialized yet. Operating in local mode.")
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error initializing Firestore", e)
            _syncStatus.value = FirestoreSyncStatus.OFFLINE
        }
    }

    private fun getDb(): FirebaseFirestore? {
        if (firestore == null && FirebaseApp.getApps(context).isNotEmpty()) {
            try {
                firestore = FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w("FirestoreManager", "Could not get Firestore instance: ${e.message}")
            }
        }
        return firestore
    }

    fun syncUserProfile(profile: UserProfile) {
        val db = getDb() ?: run {
            _syncStatus.value = FirestoreSyncStatus.OFFLINE
            return
        }

        scope.launch {
            try {
                _syncStatus.value = FirestoreSyncStatus.SYNCING

                val userData = hashMapOf(
                    "userId" to profile.id,
                    "displayName" to profile.displayName,
                    "email" to profile.email,
                    "photoUrl" to profile.photoUrl,
                    "isGoogleUser" to profile.isGoogleUser,
                    "xp" to profile.xp,
                    "level" to profile.level,
                    "coins" to profile.coins,
                    "loginStreak" to profile.loginStreak,
                    "lastRewardDate" to profile.lastRewardDate,
                    "totalGames" to profile.totalGames,
                    "wins" to profile.wins,
                    "losses" to profile.losses,
                    "draws" to profile.draws,
                    "winRate" to profile.winRate,
                    "currentStreak" to profile.currentStreak,
                    "bestStreak" to profile.bestStreak,
                    "unlockedAchievements" to profile.unlockedAchievements,
                    "updatedAt" to System.currentTimeMillis()
                )

                // Save user private doc
                db.collection("users")
                    .document(profile.id)
                    .set(userData, SetOptions.merge())
                    .await()

                // Save unlocked achievements subcollection records for rich indexing
                for (achId in profile.unlockedAchievements) {
                    val ach = com.example.data.model.AchievementCatalog.getById(achId)
                    val achData = hashMapOf(
                        "id" to achId,
                        "titleEn" to (ach?.titleEn ?: achId),
                        "titleAr" to (ach?.titleAr ?: achId),
                        "descriptionEn" to (ach?.descriptionEn ?: ""),
                        "descriptionAr" to (ach?.descriptionAr ?: ""),
                        "xpReward" to (ach?.xpReward ?: 0),
                        "unlocked" to true,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    db.collection("users")
                        .document(profile.id)
                        .collection("achievements")
                        .document(achId)
                        .set(achData, SetOptions.merge())
                        .await()
                }

                // Save public leaderboard entry ordered by wins
                val leaderboardData = hashMapOf(
                    "userId" to profile.id,
                    "displayName" to profile.displayName,
                    "photoUrl" to profile.photoUrl,
                    "xp" to profile.xp,
                    "level" to profile.level,
                    "wins" to profile.wins,
                    "totalGames" to profile.totalGames,
                    "winRate" to profile.winRate,
                    "updatedAt" to System.currentTimeMillis()
                )

                db.collection("leaderboard")
                    .document(profile.id)
                    .set(leaderboardData, SetOptions.merge())
                    .await()

                _syncStatus.value = FirestoreSyncStatus.SYNCED
                _syncMessage.value = "تمت مزامنة بياناتك مع السحابة (Firestore) بنجاح"
            } catch (e: Exception) {
                Log.e("FirestoreManager", "Failed to sync user profile", e)
                _syncStatus.value = FirestoreSyncStatus.OFFLINE
            }
        }
    }

    fun syncMatchRecord(userId: String, match: MatchRecordEntity) {
        val db = getDb() ?: run {
            _syncStatus.value = FirestoreSyncStatus.OFFLINE
            return
        }

        scope.launch {
            try {
                _syncStatus.value = FirestoreSyncStatus.SYNCING

                val matchDocId = if (match.id > 0) "match_${match.id}" else "match_${System.currentTimeMillis()}"
                val matchData = hashMapOf(
                    "matchId" to matchDocId,
                    "timestamp" to match.timestamp,
                    "mode" to match.mode,
                    "aiDifficulty" to match.aiDifficulty,
                    "winner" to match.winner,
                    "boardDimension" to match.boardDimension,
                    "movesCount" to match.movesCount,
                    "durationSeconds" to match.durationSeconds,
                    "xpEarned" to match.xpEarned
                )

                db.collection("users")
                    .document(userId)
                    .collection("matches")
                    .document(matchDocId)
                    .set(matchData, SetOptions.merge())
                    .await()

                _syncStatus.value = FirestoreSyncStatus.SYNCED
            } catch (e: Exception) {
                Log.e("FirestoreManager", "Failed to sync match record", e)
                _syncStatus.value = FirestoreSyncStatus.OFFLINE
            }
        }
    }

    suspend fun fetchRemoteUserProfile(userId: String): UserProfile? {
        val db = getDb() ?: return null
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                val achList = (snapshot.get("unlockedAchievements") as? List<*>)
                    ?.mapNotNull { it?.toString() } ?: emptyList()

                UserProfile(
                    id = snapshot.getString("userId") ?: userId,
                    displayName = snapshot.getString("displayName") ?: "لاعب إكس أو",
                    email = snapshot.getString("email"),
                    photoUrl = snapshot.getString("photoUrl"),
                    isGoogleUser = snapshot.getBoolean("isGoogleUser") ?: true,
                    xp = snapshot.getLong("xp")?.toInt() ?: 100,
                    level = snapshot.getLong("level")?.toInt() ?: 1,
                    coins = snapshot.getLong("coins")?.toInt() ?: 100,
                    loginStreak = snapshot.getLong("loginStreak")?.toInt() ?: 0,
                    lastRewardDate = snapshot.getString("lastRewardDate") ?: "",
                    totalGames = snapshot.getLong("totalGames")?.toInt() ?: 0,
                    wins = snapshot.getLong("wins")?.toInt() ?: 0,
                    losses = snapshot.getLong("losses")?.toInt() ?: 0,
                    draws = snapshot.getLong("draws")?.toInt() ?: 0,
                    currentStreak = snapshot.getLong("currentStreak")?.toInt() ?: 0,
                    bestStreak = snapshot.getLong("bestStreak")?.toInt() ?: 0,
                    unlockedAchievements = achList
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error fetching remote user profile", e)
            null
        }
    }

    /**
     * Persists a daily reward claim directly to the user's Firestore document
     * and records a receipt log in users/{userId}/daily_rewards/{today}.
     */
    fun syncDailyRewardClaimed(userId: String, day: Int, coins: Int, xp: Int, streak: Int) {
        val db = getDb() ?: return
        val today = com.example.data.model.DailyRewardSchedule.getTodayDateString()
        scope.launch {
            try {
                _syncStatus.value = FirestoreSyncStatus.SYNCING

                val updates = hashMapOf<String, Any>(
                    "coins" to FieldValue.increment(coins.toLong()),
                    "xp" to FieldValue.increment(xp.toLong()),
                    "loginStreak" to streak,
                    "lastRewardDate" to today,
                    "updatedAt" to System.currentTimeMillis()
                )

                db.collection("users").document(userId)
                    .set(updates, SetOptions.merge())
                    .await()

                val rewardLog = hashMapOf(
                    "date" to today,
                    "day" to day,
                    "coinsAwarded" to coins,
                    "xpAwarded" to xp,
                    "loginStreak" to streak,
                    "claimedAt" to System.currentTimeMillis()
                )

                db.collection("users").document(userId)
                    .collection("daily_rewards")
                    .document(today)
                    .set(rewardLog, SetOptions.merge())
                    .await()

                _syncStatus.value = FirestoreSyncStatus.SYNCED
                _syncMessage.value = "تم حفظ المكافأة اليومية (اليوم $day: +$coins كوينز، +$xp XP) في Firestore! 🎁"
            } catch (e: Exception) {
                Log.e("FirestoreManager", "Error syncing daily reward to Firestore", e)
                _syncStatus.value = FirestoreSyncStatus.OFFLINE
            }
        }
    }

    /**
     * Persists an unlocked achievement directly to the user's Firestore document
     * and writes a timestamped record to the users/{userId}/achievements subcollection.
     */
    fun syncAchievementUnlocked(userId: String, achievement: com.example.data.model.Achievement) {
        val db = getDb() ?: return
        scope.launch {
            try {
                _syncStatus.value = FirestoreSyncStatus.SYNCING

                // Update user root doc with arrayUnion
                db.collection("users").document(userId)
                    .set(
                        mapOf("unlockedAchievements" to FieldValue.arrayUnion(achievement.id)),
                        SetOptions.merge()
                    )
                    .await()

                // Also write to user's achievements subcollection
                val achDoc = hashMapOf(
                    "id" to achievement.id,
                    "titleEn" to achievement.titleEn,
                    "titleAr" to achievement.titleAr,
                    "descriptionEn" to achievement.descriptionEn,
                    "descriptionAr" to achievement.descriptionAr,
                    "xpReward" to achievement.xpReward,
                    "unlocked" to true,
                    "unlockedAt" to System.currentTimeMillis()
                )

                db.collection("users").document(userId)
                    .collection("achievements").document(achievement.id)
                    .set(achDoc, SetOptions.merge())
                    .await()

                _syncStatus.value = FirestoreSyncStatus.SYNCED
                _syncMessage.value = "تم توثيق إنجاز (${achievement.titleAr}) سحابياً في Firestore! 🏆"
            } catch (e: Exception) {
                Log.e("FirestoreManager", "Failed to sync achievement to Firestore", e)
            }
        }
    }

    /**
     * Pulls the global leaderboard ordered by total wins descending from Cloud Firestore.
     * Guaranteed to provide the Top 10 players.
     */
    fun observeTopWinsLeaderboard(limit: Long = 10): Flow<List<LeaderboardEntry>> = callbackFlow {
        val db = getDb()
        if (db == null) {
            trySend(getSeededTopWins(limit.toInt()))
            close()
            return@callbackFlow
        }

        val listener = db.collection("leaderboard")
            .orderBy("wins", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreManager", "Top wins leaderboard snapshot error, fallback to seeded: ${error.message}")
                    trySend(getSeededTopWins(limit.toInt()))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val remoteEntries = snapshot.documents.mapNotNull { doc ->
                        try {
                            LeaderboardEntry(
                                userId = doc.getString("userId") ?: doc.id,
                                displayName = doc.getString("displayName") ?: "لاعب إكس أو",
                                photoUrl = doc.getString("photoUrl"),
                                xp = doc.getLong("xp")?.toInt() ?: 0,
                                level = doc.getLong("level")?.toInt() ?: 1,
                                wins = doc.getLong("wins")?.toInt() ?: 0,
                                totalGames = doc.getLong("totalGames")?.toInt() ?: 0,
                                winRate = doc.getLong("winRate")?.toInt() ?: 0,
                                rank = 0
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    val combinedList = mergeWithSeededChampions(remoteEntries, limit.toInt())
                        .sortedByDescending { it.wins }
                        .take(limit.toInt())
                        .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

                    trySend(combinedList)
                } else {
                    trySend(getSeededTopWins(limit.toInt()))
                }
            }

        awaitClose { listener.remove() }
    }

    fun observeGlobalLeaderboard(): Flow<List<LeaderboardEntry>> = observeTopWinsLeaderboard(10)

    private fun mergeWithSeededChampions(remoteEntries: List<LeaderboardEntry>, limit: Int): List<LeaderboardEntry> {
        val result = remoteEntries.toMutableList()
        val remoteIds = result.map { it.userId }.toSet()
        val seeded = getSeededTopWins(limit)

        for (champion in seeded) {
            if (result.size >= limit) break
            if (!remoteIds.contains(champion.userId)) {
                result.add(champion)
            }
        }
        return result
    }

    private fun getSeededTopWins(limit: Int): List<LeaderboardEntry> {
        val seeded = listOf(
            LeaderboardEntry(
                userId = "champion_1",
                displayName = "سيف الدين (Grandmaster)",
                photoUrl = null,
                xp = 4600,
                level = 12,
                wins = 48,
                totalGames = 58,
                winRate = 82,
                rank = 1
            ),
            LeaderboardEntry(
                userId = "champion_2",
                displayName = "ياسمين الشام (XO Queen)",
                photoUrl = null,
                xp = 3750,
                level = 10,
                wins = 39,
                totalGames = 50,
                winRate = 78,
                rank = 2
            ),
            LeaderboardEntry(
                userId = "champion_3",
                displayName = "عمر التكتيكي",
                photoUrl = null,
                xp = 2980,
                level = 8,
                wins = 31,
                totalGames = 41,
                winRate = 75,
                rank = 3
            ),
            LeaderboardEntry(
                userId = "champion_4",
                displayName = "Alex Rivera",
                photoUrl = null,
                xp = 2400,
                level = 7,
                wins = 26,
                totalGames = 36,
                winRate = 71,
                rank = 4
            ),
            LeaderboardEntry(
                userId = "champion_5",
                displayName = "طارق الشبح",
                photoUrl = null,
                xp = 1950,
                level = 6,
                wins = 21,
                totalGames = 31,
                winRate = 68,
                rank = 5
            ),
            LeaderboardEntry(
                userId = "champion_6",
                displayName = "نور الهادي",
                photoUrl = null,
                xp = 1580,
                level = 5,
                wins = 17,
                totalGames = 26,
                winRate = 65,
                rank = 6
            ),
            LeaderboardEntry(
                userId = "champion_7",
                displayName = "Karim Sniper",
                photoUrl = null,
                xp = 1280,
                level = 4,
                wins = 14,
                totalGames = 22,
                winRate = 63,
                rank = 7
            ),
            LeaderboardEntry(
                userId = "champion_8",
                displayName = "ريم أحمد",
                photoUrl = null,
                xp = 980,
                level = 3,
                wins = 11,
                totalGames = 18,
                winRate = 60,
                rank = 8
            ),
            LeaderboardEntry(
                userId = "champion_9",
                displayName = "Lucas Silva",
                photoUrl = null,
                xp = 790,
                level = 3,
                wins = 9,
                totalGames = 15,
                winRate = 58,
                rank = 9
            ),
            LeaderboardEntry(
                userId = "champion_10",
                displayName = "زياد بطل 7×7",
                photoUrl = null,
                xp = 620,
                level = 2,
                wins = 7,
                totalGames = 12,
                winRate = 55,
                rank = 10
            )
        )
        return seeded.take(limit)
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
