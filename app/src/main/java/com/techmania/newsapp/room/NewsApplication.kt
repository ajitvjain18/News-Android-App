package com.techmania.newsapp.room

import android.app.Application

class NewsApplication : Application() {
    val database by lazy { NewsDatabase.getdatabase(this) }
    val repository by lazy { NewsRepository(database.articleDao()) }
}