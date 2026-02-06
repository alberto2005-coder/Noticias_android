package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.NewsEntity
import com.example.myapplication.data.NewsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _newsList = MutableLiveData<List<NewsEntity>>()
    val newsList: LiveData<List<NewsEntity>> = _newsList

    private val _selectedNews = MutableLiveData<NewsEntity?>()
    val selectedNews: LiveData<NewsEntity?> = _selectedNews

    private var collectJob: Job? = null

    init {
        loadNewsSortedByDate()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshNews()
        }
    }

    fun loadNewsSortedByDate() {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            repository.allNewsDate.collectLatest {
                _newsList.value = it
            }
        }
    }

    fun loadNewsSortedByImportance() {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            repository.allNewsImportance.collectLatest {
                _newsList.value = it
            }
        }
    }

    fun search(query: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            repository.search(query).collectLatest {
                _newsList.value = it
            }
        }
    }

    fun selectNews(news: NewsEntity) {
        _selectedNews.value = news
    }
}

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
