package com.example.data.model

enum class Player(val symbol: String) {
    X("X"),
    O("O");

    fun opponent(): Player = if (this == X) O else X
}

enum class GameMode {
    VS_AI,
    TWO_PLAYERS
}

enum class AIDifficulty {
    EASY,
    MEDIUM,
    HARD,
    MASTER
}

enum class BoardSize(
    val dimension: Int,
    val requiredToWin: Int,
    val titleAr: String,
    val titleEn: String,
    val difficultyLabelAr: String,
    val difficultyLabelEn: String,
    val difficultyRating: Int
) {
    SIZE_3X3(3, 3, "3×3 (9 مربعات)", "3×3 (9 Squares)", "سهل كلاسيكي", "Classic", 1),
    SIZE_4X4(4, 4, "4×4 (16 مربع)", "4×4 (16 Squares)", "متوسط", "Moderate", 2),
    SIZE_5X5(5, 4, "5×5 (25 مربع)", "5×5 (25 Squares)", "صعب تكتيكي", "Tactical Hard", 3),
    SIZE_6X6(6, 5, "6×6 (36 مربع)", "6×6 (36 Squares)", "خبير", "Expert", 4),
    SIZE_7X7(7, 5, "7×7 (49 مربع)", "7×7 (49 Squares)", "التحدي الأقصى", "Grandmaster", 5);

    val totalSquares: Int get() = dimension * dimension

    fun nextBiggerSize(): BoardSize? = when (this) {
        SIZE_3X3 -> SIZE_4X4
        SIZE_4X4 -> SIZE_5X5
        SIZE_5X5 -> SIZE_6X6
        SIZE_6X6 -> SIZE_7X7
        SIZE_7X7 -> null
    }

    fun nextSmallerSize(): BoardSize? = when (this) {
        SIZE_7X7 -> SIZE_6X6
        SIZE_6X6 -> SIZE_5X5
        SIZE_5X5 -> SIZE_4X4
        SIZE_4X4 -> SIZE_3X3
        SIZE_3X3 -> null
    }
}

enum class GameThemeType {
    NEON_CYBER,
    MINIMAL_DARK,
    ARCADE_GOLD,
    COSMIC_PURPLE
}

enum class AppLanguage {
    ARABIC,
    ENGLISH
}

data class WinningLine(
    val winningCells: List<Int>,
    val lineType: LineType
)

enum class LineType {
    ROW,
    COLUMN,
    DIAGONAL_MAIN,
    DIAGONAL_ANTI
}

data class UserProfile(
    val id: String = "guest_user",
    val displayName: String = "لاعب إكس أو",
    val email: String? = null,
    val photoUrl: String? = null,
    val isGoogleUser: Boolean = false,
    val xp: Int = 120,
    val level: Int = 1,
    val coins: Int = 100,
    val loginStreak: Int = 0,
    val lastRewardDate: String = "",
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val unlockedAchievements: List<String> = emptyList()
) {
    val winRate: Int
        get() = if (totalGames > 0) ((wins.toFloat() / totalGames) * 100).toInt() else 0

    val rankTitleAr: String
        get() = when {
            xp >= 2000 -> "أسطورة إكس أو 👑"
            xp >= 1000 -> "ماسي 💎"
            xp >= 500 -> "بلاتينيوم ⚔️"
            xp >= 250 -> "ذهبي 🏆"
            xp >= 100 -> "فضي 🥈"
            else -> "مبتدئ برونزي 🥉"
        }

    val rankTitleEn: String
        get() = when {
            xp >= 2000 -> "XO Legend 👑"
            xp >= 1000 -> "Diamond 💎"
            xp >= 500 -> "Platinum ⚔️"
            xp >= 250 -> "Gold 🏆"
            xp >= 100 -> "Silver 🥈"
            else -> "Bronze Rookie 🥉"
        }
}

enum class FirestoreSyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE
}

data class LeaderboardEntry(
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val xp: Int = 0,
    val level: Int = 1,
    val wins: Int = 0,
    val totalGames: Int = 0,
    val winRate: Int = 0,
    val rank: Int = 0
)
