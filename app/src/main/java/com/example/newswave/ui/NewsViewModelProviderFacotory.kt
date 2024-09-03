package com.example.newswave.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.newswave.repository.NewsRepository

class NewsViewModelProviderFacotory(val app:Application, val repository: NewsRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return NewsViewModel(app,repository)as T
    }
}