package com.techmania.newsapp.retrofit

import androidx.room.Entity
import androidx.room.PrimaryKey

data class NewsResponse(val articles: List<Article>)

@Entity(tableName = "articles")
data class Article(
    val author: String?,
    val title: String,
    val description: String?,
    @PrimaryKey val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    var localImagePath: String? = null,
    var isOffline: Boolean = false
)

