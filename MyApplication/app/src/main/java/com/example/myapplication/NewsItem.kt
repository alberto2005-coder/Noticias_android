package com.example.myapplication

import java.io.Serializable

data class NewsItem(
    val title: String,
    val shortDescription: String,
    val thumbnailUrl: String,
    val content: String,
    val largeImageUrl: String
) : Serializable
