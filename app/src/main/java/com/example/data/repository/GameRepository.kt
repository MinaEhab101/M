package com.example.data.repository

import com.example.data.local.GameHistoryDao
import com.example.data.local.MatchRecordEntity
import kotlinx.coroutines.flow.Flow

class GameRepository(private val dao: GameHistoryDao) {
    val historyRecords: Flow<List<MatchRecordEntity>> = dao.getAllRecords()

    suspend fun saveMatch(
        mode: String,
        aiDifficulty: String?,
        winner: String,
        boardDimension: Int,
        movesCount: Int,
        durationSeconds: Int,
        xpEarned: Int
    ): Long {
        val entity = MatchRecordEntity(
            mode = mode,
            aiDifficulty = aiDifficulty,
            winner = winner,
            boardDimension = boardDimension,
            movesCount = movesCount,
            durationSeconds = durationSeconds,
            xpEarned = xpEarned
        )
        return dao.insertRecord(entity)
    }

    suspend fun clearAllHistory() {
        dao.clearHistory()
    }
}
