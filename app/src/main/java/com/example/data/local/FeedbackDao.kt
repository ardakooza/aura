package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity): Long

    @Query("SELECT * FROM user_feedbacks ORDER BY timestamp DESC")
    fun getAllFeedbacks(): Flow<List<FeedbackEntity>>

    @Query("SELECT AVG(rating) FROM user_feedbacks")
    fun getAverageRating(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM user_feedbacks")
    fun getFeedbackCount(): Flow<Int>
}
