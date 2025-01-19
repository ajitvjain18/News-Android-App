package com.techmania.newsapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.techmania.newsapp.retrofit.ApiServiceEngine
import com.techmania.newsapp.retrofit.Article
import com.techmania.newsapp.retrofit.NewsResponse
import com.techmania.newsapp.retrofit.NewsService
import com.techmania.newsapp.room.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeViewModel(private val application: Application, private val repository: NewsRepository) :
    BaseViewModel(application) {

    private val apiService = ApiServiceEngine.buildService(NewsService::class.java)

    private val _newsLiveData: MutableLiveData<List<Article>> = MutableLiveData()
    val newsLiveData: LiveData<List<Article>> = _newsLiveData

    private val _offlinenewsLiveData: MutableLiveData<List<Article>> = MutableLiveData()
    val offlinenewsLiveData: LiveData<List<Article>> = _offlinenewsLiveData

    private lateinit var selectedArticle: Article

    lateinit var selectedOfflineArticle: Article


    fun loadNews(query: String) {
        _loadingLiveData.postValue(true)
        val call = apiService.getNews(
            query,
            "2025-01-18",
            "publishedAt",
            "8f6b0b2da032470e90cba2a2722b4b9c"
        )
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { newsResponse ->
                        _newsLiveData.postValue(newsResponse.articles)
                        _loadingLiveData.postValue(false)
                    }
                } else {
                    _errorMessageLiveData.postValue(response.message())
                    _loadingLiveData.postValue(false)
                }
            }

            override fun onFailure(call: Call<NewsResponse>, response: Throwable) {
                _errorMessageLiveData.postValue(response.message)
                _loadingLiveData.postValue(false)
            }
        })
    }

    private fun saveImageLocally(imageUrl: String): String? {
        return try {
            val file = Glide.with(application)
                .asFile()
                .load(imageUrl)
                .submit()
                .get()
            val storageDir = File(application.cacheDir, "images")
            if (!storageDir.exists()) storageDir.mkdir()
            val localFile = File(storageDir, file.name)
            file.copyTo(localFile, overwrite = true)

            localFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun storeArticle(article: Article) {
        selectedArticle = article
    }

    fun storeOfflineArticle(article: Article) {
        selectedOfflineArticle = article
    }

    fun saveArticle() {
        viewModelScope.launch(Dispatchers.IO) {
            val article = selectedArticle
            val localImagePath = article.urlToImage?.let {
                saveImageLocally(it)
            }
            article.let {
                repository.insertOrUpdateArticle(it)
                repository.updateArticle(selectedArticle.url, true, localImagePath)
            }
        }
    }

    fun loadOffline() {
        viewModelScope.launch(Dispatchers.IO) {
            val offlineList = repository.loadOfflineArticles()
            _offlinenewsLiveData.postValue(offlineList)
        }
    }

    fun deleteArticelFromLocal(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteArticle(url)
            loadOffline()
        }
    }
}

class HomeViewModelFactory(
    private var repository: NewsRepository,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application, repository) as T
        } else {
            throw IllegalArgumentException("Unknown viewmodel")
        }
    }
}


