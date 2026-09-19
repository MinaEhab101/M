package com.example.ui.localization

import com.example.data.model.AIDifficulty
import com.example.data.model.AppLanguage
import com.example.data.model.BoardSize
import com.example.data.model.GameMode

object Strings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إكس أو ماستر"
        AppLanguage.ENGLISH -> "XO Master"
    }

    fun playVsAI(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "ضد الكمبيوتر (AI)"
        AppLanguage.ENGLISH -> "vs Computer (AI)"
    }

    fun twoPlayers(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "لاعبان (نفس الجهاز)"
        AppLanguage.ENGLISH -> "2 Players (Local)"
    }

    fun speedChallenge(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تحدي السرعة ⚡"
        AppLanguage.ENGLISH -> "Speed Challenge ⚡"
    }

    fun leaderboards(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الإحصائيات والسجل"
        AppLanguage.ENGLISH -> "Stats & History"
    }

    fun profile(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الملف الشخصي"
        AppLanguage.ENGLISH -> "Profile"
    }

    fun settings(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الإعدادات"
        AppLanguage.ENGLISH -> "Settings"
    }

    fun googleSignIn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الدخول بحساب Google"
        AppLanguage.ENGLISH -> "Sign in with Google"
    }

    fun googleSignInDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "احفظ نقاطك وسجلك وتنافس عالمياً على متجر Google Play!"
        AppLanguage.ENGLISH -> "Save XP, track your stats and compete on Google Play!"
    }

    fun signedInAs(lang: AppLanguage, name: String) = when (lang) {
        AppLanguage.ARABIC -> "مسجل كـ: $name"
        AppLanguage.ENGLISH -> "Signed in as: $name"
    }

    fun signOut(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الخروج"
        AppLanguage.ENGLISH -> "Sign Out"
    }

    fun playStoreReady(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "جاهز لمتجر Google Play"
        AppLanguage.ENGLISH -> "Google Play Ready"
    }

    fun difficulty(lang: AppLanguage, diff: AIDifficulty) = when (lang) {
        AppLanguage.ARABIC -> when (diff) {
            AIDifficulty.EASY -> "سهل"
            AIDifficulty.MEDIUM -> "متوسط"
            AIDifficulty.HARD -> "صعب"
            AIDifficulty.MASTER -> "مستحيل (خبير)"
        }
        AppLanguage.ENGLISH -> when (diff) {
            AIDifficulty.EASY -> "Easy"
            AIDifficulty.MEDIUM -> "Medium"
            AIDifficulty.HARD -> "Hard"
            AIDifficulty.MASTER -> "Master (Impossible)"
        }
    }

    fun boardSizeLabel(lang: AppLanguage, size: BoardSize) = when (lang) {
        AppLanguage.ARABIC -> when (size) {
            BoardSize.SIZE_3X3 -> "كلاسيكي 3×3 (9 مربعات)"
            BoardSize.SIZE_4X4 -> "متقدم 4×4 (16 مربع)"
            BoardSize.SIZE_5X5 -> "تكتيكي 5×5 (25 مربع)"
            BoardSize.SIZE_6X6 -> "خبير 6×6 (36 مربع)"
            BoardSize.SIZE_7X7 -> "أسطوري 7×7 (49 مربع)"
        }
        AppLanguage.ENGLISH -> when (size) {
            BoardSize.SIZE_3X3 -> "Classic 3x3 (9 Squares)"
            BoardSize.SIZE_4X4 -> "Advanced 4x4 (16 Squares)"
            BoardSize.SIZE_5X5 -> "Tactical 5x5 (25 Squares)"
            BoardSize.SIZE_6X6 -> "Expert 6x6 (36 Squares)"
            BoardSize.SIZE_7X7 -> "Grandmaster 7x7 (49 Squares)"
        }
    }

    fun turnText(lang: AppLanguage, isX: Boolean, mode: GameMode) = when (lang) {
        AppLanguage.ARABIC -> if (isX) "دور اللاعب (X)" else if (mode == GameMode.VS_AI) "تفكير الذكاء الاصطناعي (O)..." else "دور اللاعب (O)"
        AppLanguage.ENGLISH -> if (isX) "Player (X)'s Turn" else if (mode == GameMode.VS_AI) "AI is thinking (O)..." else "Player (O)'s Turn"
    }

    fun xWins(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🎉 فاز اللاعب (X)!"
        AppLanguage.ENGLISH -> "🎉 Player (X) Won!"
    }

    fun oWins(lang: AppLanguage, mode: GameMode) = when (lang) {
        AppLanguage.ARABIC -> if (mode == GameMode.VS_AI) "🤖 فاز الذكاء الاصطناعي (O)!" else "🎉 فاز اللاعب (O)!"
        AppLanguage.ENGLISH -> if (mode == GameMode.VS_AI) "🤖 AI Won (O)!" else "🎉 Player (O) Won!"
    }

    fun drawGame(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🤝 تعادل رائع!"
        AppLanguage.ENGLISH -> "🤝 It's a Draw!"
    }

    fun playAgain(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "العب مجدداً"
        AppLanguage.ENGLISH -> "Play Again"
    }

    fun mainMenu(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "القائمة الرئيسية"
        AppLanguage.ENGLISH -> "Main Menu"
    }

    fun score(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "النتيجة"
        AppLanguage.ENGLISH -> "Score"
    }

    fun privacyPolicy(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "سياسة الخصوصية (Google Play)"
        AppLanguage.ENGLISH -> "Privacy Policy (Google Play)"
    }

    fun privacyPolicyText(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تلتزم لعبة XO Master بحماية خصوصيتك التامة وفقاً لسياسات Google Play Developer. نحن نستخدم تسجيل الدخول بحساب Google لحفظ إنجازاتك ونقاطك بأمان فقط. لا نبيع ولا نشارك أي بيانات شخصية مع أطراف ثالثة."
        AppLanguage.ENGLISH -> "XO Master respects your privacy in compliance with Google Play Developer Policies. Google Sign-In is solely used to safely retain your game XP, ranks, and achievements. We do not collect or share personal data with any third parties."
    }

    fun aboutApp(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "حول اللعبة"
        AppLanguage.ENGLISH -> "About App"
    }

    fun aboutAppText(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "XO Master - النسخة المطورة لمتجر Google Play.\nتم التطوير بواسطة: مينا (Ehab Mina)\nمحرك ذكاء اصطناعي Minimax وتصميم واجهات حديثة Material Design 3."
        AppLanguage.ENGLISH -> "XO Master - Elevated for Google Play Store.\nDeveloped by: Ehab Mina\nPowered by Minimax AI algorithm and modern Material Design 3."
    }

    fun globalLeaderboard(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "المتصدرون عالمياً (أفضل 10)"
        AppLanguage.ENGLISH -> "Global Leaderboard (Top 10)"
    }

    fun topWinsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أفضل 10 لاعبين حسب الانتصارات 🏆"
        AppLanguage.ENGLISH -> "Top 10 Players by Wins 🏆"
    }

    fun topWinsSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "سحابي مباشر من Cloud Firestore"
        AppLanguage.ENGLISH -> "Live sync from Cloud Firestore"
    }

    fun totalWins(lang: AppLanguage, count: Int) = when (lang) {
        AppLanguage.ARABIC -> "$count فوز"
        AppLanguage.ENGLISH -> "$count Wins"
    }

    fun viewFullLeaderboard(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "عرض القائمة الكاملة"
        AppLanguage.ENGLISH -> "View Full Leaderboard"
    }

    fun dailyRewardTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مكافأة تسجيل الدخول اليومية 🎁"
        AppLanguage.ENGLISH -> "Daily Login Reward 🎁"
    }

    fun dailyRewardSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "احصل على كوينز و XP يومياً ومزامنة ملفك في Firestore"
        AppLanguage.ENGLISH -> "Claim daily coins & XP, synced directly with Firestore"
    }

    fun claimDailyReward(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "استلام المكافأة الآن 🎁"
        AppLanguage.ENGLISH -> "Claim Reward Now 🎁"
    }

    fun dailyRewardClaimed(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تم الاستلام اليوم ✅"
        AppLanguage.ENGLISH -> "Claimed Today ✅"
    }

    fun loginStreakText(lang: AppLanguage, streak: Int) = when (lang) {
        AppLanguage.ARABIC -> "سلسلة الحضور: $streak أيام 🔥"
        AppLanguage.ENGLISH -> "Login Streak: $streak Days 🔥"
    }

    fun dayLabel(lang: AppLanguage, day: Int) = when (lang) {
        AppLanguage.ARABIC -> "اليوم $day"
        AppLanguage.ENGLISH -> "Day $day"
    }

    fun jackpot(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الجائزة الكبرى 👑"
        AppLanguage.ENGLISH -> "Mega Jackpot 👑"
    }

    fun coinsLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "كوينز"
        AppLanguage.ENGLISH -> "Coins"
    }
}
