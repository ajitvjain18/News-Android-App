package com.techmania.newsapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techmania.newsapp.retrofit.Article

@Database(entities = [Article::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object{
        @Volatile
        private var INSTANCE : NewsDatabase? = null
        private val DATABASE_NAME = "news.db"

        fun getdatabase(context: Context) : NewsDatabase{
            return  INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,NewsDatabase::class.java,DATABASE_NAME).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
