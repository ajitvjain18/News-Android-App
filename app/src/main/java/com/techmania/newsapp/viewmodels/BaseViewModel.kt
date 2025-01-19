package com.techmania.newsapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.techmania.newsapp.room.NewsRepository

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val _errorMessageLiveData = SingleLiveEvent<String>()
    val errorMessageLiveData: LiveData<String> = _errorMessageLiveData

    protected val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData
}
