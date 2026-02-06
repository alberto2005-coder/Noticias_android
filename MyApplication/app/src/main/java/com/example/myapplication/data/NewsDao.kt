package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_table ORDER BY publishedDate DESC")
    fun getAllSortedByDate(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_table ORDER BY importance DESC")
    fun getAllSortedByImportance(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_table WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNews(query: String): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Query("DELETE FROM news_table")
    suspend fun deleteAll()
}
