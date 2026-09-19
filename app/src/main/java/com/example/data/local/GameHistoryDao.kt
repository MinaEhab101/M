package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MatchRecordEntity): Long

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllRecords(): Flow<List<MatchRecordEntity>>

    @Query("SELECT COUNT(*) FROM match_history")
    suspend fun getTotalMatches(): Int

    @Query("SELECT COUNT(*) FROM match_history WHERE winner = 'X'")
    suspend fun getXWins(): Int

    @Query("SELECT COUNT(*) FROM match_history WHERE winner = 'O'")
    suspend fun getOWins(): Int

    @Query("SELECT COUNT(*) FROM match_history WHERE winner = 'DRAW'")
    suspend fun getDraws(): Int

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()
}
