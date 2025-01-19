package com.techmania.newsapp.room

import com.techmania.newsapp.retrofit.Article

class NewsRepository(private val articleDao: ArticleDao) {

    suspend fun insertOrUpdateArticle(article: Article) {
        articleDao.insertArticle(article)
    }

    suspend fun updateArticle(url: String, isOffline: Boolean, localImagePath: String?) {
        articleDao.updateOfflineStatus(url, isOffline, localImagePath)
    }

    suspend fun deleteArticle(url: String) {
        articleDao.deleteArticle(url)
    }

    suspend fun loadOfflineArticles(): List<Article> {
        return articleDao.getAllArticles()
    }
}
