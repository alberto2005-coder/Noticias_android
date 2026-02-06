package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val shortDescription: String,
    val thumbnailUrl: String,
    val content: String,
    val largeImageUrl: String,
    val publishedDate: Long, // Processed from CSV or generated
    val importance: Int // 1 to 10
)
