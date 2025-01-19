package com.techmania.newsapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techmania.newsapp.retrofit.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article)

    @Query("UPDATE articles SET isOffline = :isOffline, localImagePath = :localImagePath WHERE url = :url")
    suspend fun updateOfflineStatus(url: String, isOffline: Boolean, localImagePath: String?)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): List<Article>

    @Query("DELETE FROM articles WHERE url = :url")
    suspend fun deleteArticle(url: String)
}
