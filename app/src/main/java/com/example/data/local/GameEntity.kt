package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_history")
data class MatchRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String, // "VS_AI" or "TWO_PLAYERS"
    val aiDifficulty: String?, // "EASY", "MEDIUM", "HARD", "MASTER"
    val winner: String, // "X", "O", "DRAW"
    val boardDimension: Int, // 3, 4, 5
    val movesCount: Int,
    val durationSeconds: Int,
    val xpEarned: Int
)
