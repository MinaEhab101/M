package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyRewardDay(
    val dayNumber: Int,
    val coins: Int,
    val xp: Int,
    val isJackpot: Boolean = false
) {
    val descriptionAr: String
        get() = "+$coins كوينز  •  +$xp XP"

    val descriptionEn: String
        get() = "+$coins Coins  •  +$xp XP"
}

data class DailyRewardClaimResult(
    val dayNumber: Int,
    val coinsEarned: Int,
    val xpEarned: Int,
    val totalCoins: Int,
    val totalXp: Int,
    val newLevel: Int,
    val loginStreak: Int,
    val isJackpot: Boolean
)

object DailyRewardSchedule {
    val rewards: List<DailyRewardDay> = listOf(
        DailyRewardDay(dayNumber = 1, coins = 50, xp = 30, isJackpot = false),
        DailyRewardDay(dayNumber = 2, coins = 80, xp = 50, isJackpot = false),
        DailyRewardDay(dayNumber = 3, coins = 120, xp = 75, isJackpot = false),
        DailyRewardDay(dayNumber = 4, coins = 160, xp = 100, isJackpot = false),
        DailyRewardDay(dayNumber = 5, coins = 220, xp = 150, isJackpot = false),
        DailyRewardDay(dayNumber = 6, coins = 300, xp = 200, isJackpot = false),
        DailyRewardDay(dayNumber = 7, coins = 600, xp = 400, isJackpot = true)
    )

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }

    /**
     * Determines whether user is eligible to claim a daily reward today.
     */
    fun canClaimToday(user: UserProfile): Boolean {
        val today = getTodayDateString()
        return user.lastRewardDate != today
    }

    /**
     * Determines which day in the 7-day cycle the user is on or eligible to claim next.
     */
    fun getTargetDay(user: UserProfile): Int {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        return if (user.lastRewardDate == today) {
            // Already claimed today, so show the day that was claimed
            if (user.loginStreak <= 0) 1 else ((user.loginStreak - 1) % 7) + 1
        } else if (user.lastRewardDate == yesterday) {
            // Logged in yesterday, streak continues to next day
            (user.loginStreak % 7) + 1
        } else {
            // Streak broken (or first time), resets to Day 1
            1
        }
    }

    fun getRewardForDay(dayNumber: Int): DailyRewardDay {
        val normalized = ((dayNumber - 1) % rewards.size) + 1
        return rewards.firstOrNull { it.dayNumber == normalized } ?: rewards.first()
    }
}
