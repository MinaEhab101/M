package com.example.data.model

data class Achievement(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val xpReward: Int,
    val iconName: String,
    val maxProgress: Int = 1
) {
    fun title(lang: AppLanguage): String = if (lang == AppLanguage.ARABIC) titleAr else titleEn
    fun description(lang: AppLanguage): String = if (lang == AppLanguage.ARABIC) descriptionAr else descriptionEn
}

object AchievementCatalog {
    const val FIRST_WIN = "first_win"
    const val WIN_STREAK_3 = "win_streak_3"
    const val WIN_STREAK_5 = "win_streak_5"
    const val WIN_STREAK_10 = "win_streak_10"
    const val VETERAN_10_GAMES = "veteran_10_games"
    const val BATTLE_25_GAMES = "battle_25_games"
    const val WINS_10 = "wins_10"
    const val WINS_25 = "wins_25"
    const val GRIDMASTER_7X7 = "gridmaster_7x7"
    const val TACTICIAN_5X5 = "tactician_5x5"
    const val AI_MASTER_SLAYER = "ai_master_slayer"
    const val LEVEL_5 = "level_5"
    const val CLOUD_SYNCED = "cloud_synced"
    const val SPEED_DEMON = "speed_demon"

    val allAchievements: List<Achievement> = listOf(
        Achievement(
            id = FIRST_WIN,
            titleAr = "النصر الأول 🏆",
            titleEn = "First Win 🏆",
            descriptionAr = "حقق أول انتصار لك في اللعبة",
            descriptionEn = "Achieve your very first victory in the game",
            xpReward = 50,
            iconName = "emoji_events",
            maxProgress = 1
        ),
        Achievement(
            id = WIN_STREAK_3,
            titleAr = "سلسلة نارية (3 متتالية) 🔥",
            titleEn = "Hot Streak (3 in a row) 🔥",
            descriptionAr = "حقق 3 انتصارات متتالية بدون أي هزيمة",
            descriptionEn = "Win 3 matches in a row without defeat",
            xpReward = 50,
            iconName = "whatshot",
            maxProgress = 3
        ),
        Achievement(
            id = WIN_STREAK_5,
            titleAr = "لا يُقهر (5 متتالية) ⚡",
            titleEn = "Unstoppable (5 in a row) ⚡",
            descriptionAr = "حقق 5 انتصارات متتالية متواصلة",
            descriptionEn = "Achieve a 5-game winning streak",
            xpReward = 100,
            iconName = "bolt",
            maxProgress = 5
        ),
        Achievement(
            id = WIN_STREAK_10,
            titleAr = "10 انتصارات متتالية 👑",
            titleEn = "10 Wins in a Row 👑",
            descriptionAr = "إنجاز أسطوري: حقق 10 انتصارات متتالية متتالية!",
            descriptionEn = "Legendary milestone: win 10 matches in a row!",
            xpReward = 250,
            iconName = "crown",
            maxProgress = 10
        ),
        Achievement(
            id = VETERAN_10_GAMES,
            titleAr = "مخضرم التكتيك (10 مباريات) 🎖️",
            titleEn = "Tactical Veteran (10 Games) 🎖️",
            descriptionAr = "خض وأكمل 10 مباريات كاملة في مسيرتك",
            descriptionEn = "Complete 10 total matches in your career",
            xpReward = 50,
            iconName = "military_tech",
            maxProgress = 10
        ),
        Achievement(
            id = BATTLE_25_GAMES,
            titleAr = "المنافس البارع (25 مباراة) ⚔️",
            titleEn = "Battle Hardened (25 Games) ⚔️",
            descriptionAr = "أكمل 25 مباراة تنافسية",
            descriptionEn = "Complete 25 competitive matches",
            xpReward = 100,
            iconName = "swords",
            maxProgress = 25
        ),
        Achievement(
            id = WINS_10,
            titleAr = "عشرية الانتصارات (10 فوز) 🌟",
            titleEn = "Decade of Victories (10 Wins) 🌟",
            descriptionAr = "احصد 10 انتصارات في مسيرتك الاحترافية",
            descriptionEn = "Accumulate 10 total career wins",
            xpReward = 100,
            iconName = "star",
            maxProgress = 10
        ),
        Achievement(
            id = WINS_25,
            titleAr = "البطل الفضي (25 فوز) 🥈",
            titleEn = "Silver Champion (25 Wins) 🥈",
            descriptionAr = "احصد 25 فوزاً لتثبت سيطرتك التكتيكية",
            descriptionEn = "Reach 25 total career wins",
            xpReward = 200,
            iconName = "shield",
            maxProgress = 25
        ),
        Achievement(
            id = GRIDMASTER_7X7,
            titleAr = "سيد المربعات 7×7 (49 مربع) 🏁",
            titleEn = "Gridmaster 7x7 (49 Squares) 🏁",
            descriptionAr = "حقق الفوز على رقعة التحدي الأقصى 7×7",
            descriptionEn = "Win a match on the supreme 7x7 board",
            xpReward = 150,
            iconName = "grid_on",
            maxProgress = 1
        ),
        Achievement(
            id = TACTICIAN_5X5,
            titleAr = "تكتيك الأبعاد 5×5 📐",
            titleEn = "Expansive Tactician 5x5 📐",
            descriptionAr = "فز بمباراة على شبكة 5×5 الواسعة",
            descriptionEn = "Win a match on the expansive 5x5 grid",
            xpReward = 75,
            iconName = "grid_view",
            maxProgress = 1
        ),
        Achievement(
            id = AI_MASTER_SLAYER,
            titleAr = "قاهر الذكاء الخارق 🤖",
            titleEn = "AI Master Slayer 🤖",
            descriptionAr = "اهزم الذكاء الاصطناعي على مستوى الصعوبة الأقصى (Master)",
            descriptionEn = "Defeat the AI on the highest Master difficulty level",
            xpReward = 150,
            iconName = "smart_toy",
            maxProgress = 1
        ),
        Achievement(
            id = LEVEL_5,
            titleAr = "جراند ماستر صاعد (المستوى 5) 💎",
            titleEn = "Rising Grandmaster (Level 5) 💎",
            descriptionAr = "ارتقِ بمستواك إلى المستوى 5 باكتساب نقاط الخبرة",
            descriptionEn = "Reach player Level 5 by accumulating XP",
            xpReward = 100,
            iconName = "diamond",
            maxProgress = 5
        ),
        Achievement(
            id = CLOUD_SYNCED,
            titleAr = "المزامنة السحابية الكاملة ☁️",
            titleEn = "Full Cloud Sync ☁️",
            descriptionAr = "سجل الدخول بحساب جوجل ومزامنة ملفك مع Cloud Firestore",
            descriptionEn = "Sign in with Google and sync your profile to Cloud Firestore",
            xpReward = 75,
            iconName = "cloud_done",
            maxProgress = 1
        ),
        Achievement(
            id = SPEED_DEMON,
            titleAr = "الصاعقة السريعة (< 15 ثانية) ⏱️",
            titleEn = "Speed Demon (< 15 Seconds) ⏱️",
            descriptionAr = "احسم الفوز في مباراة خلال 15 ثانية أو أقل",
            descriptionEn = "Clinch a victory in 15 seconds or less",
            xpReward = 50,
            iconName = "timer",
            maxProgress = 1
        )
    )

    fun getById(id: String): Achievement? = allAchievements.find { it.id == id }

    fun calculateProgress(achievement: Achievement, user: UserProfile): Int {
        return when (achievement.id) {
            FIRST_WIN -> user.wins.coerceAtMost(1)
            WIN_STREAK_3 -> user.bestStreak.coerceAtMost(3)
            WIN_STREAK_5 -> user.bestStreak.coerceAtMost(5)
            WIN_STREAK_10 -> user.bestStreak.coerceAtMost(10)
            VETERAN_10_GAMES -> user.totalGames.coerceAtMost(10)
            BATTLE_25_GAMES -> user.totalGames.coerceAtMost(25)
            WINS_10 -> user.wins.coerceAtMost(10)
            WINS_25 -> user.wins.coerceAtMost(25)
            LEVEL_5 -> user.level.coerceAtMost(5)
            CLOUD_SYNCED -> if (user.isGoogleUser) 1 else 0
            GRIDMASTER_7X7, TACTICIAN_5X5, AI_MASTER_SLAYER, SPEED_DEMON -> {
                if (user.unlockedAchievements.contains(achievement.id)) 1 else 0
            }
            else -> if (user.unlockedAchievements.contains(achievement.id)) 1 else 0
        }
    }

    fun getProgressValues(achievement: Achievement, user: UserProfile): Pair<Int, Int> {
        val current = calculateProgress(achievement, user)
        val target = achievement.maxProgress
        return current to target
    }

    fun calculateProgressRatio(achievement: Achievement, user: UserProfile): Float {
        val current = calculateProgress(achievement, user)
        val target = achievement.maxProgress
        return if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 1f
    }
}
