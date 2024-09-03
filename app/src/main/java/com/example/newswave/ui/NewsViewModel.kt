package com.example.newswave.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newswave.models.Article
import com.example.newswave.models.NewsResponse
import com.example.newswave.repository.NewsRepository
import com.example.newswave.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response


class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {

        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
     //   Log.d("TEST", "Fetching headlines for country: $countryCode")

        headlinesInternet(countryCode)
    //    Log.d("TEST" ,"headline  is ${headlines.value}")

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
        Log.d("Tag","search is ${searchNews.value}")
    }

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
//        Log.d("NewsViewModel", "Headlines Response: ${response.code()} ${response.message()}")

        if (response.isSuccessful) {
            response.body()?.let { resultRespnse ->
                headlinesPage++
                if (headlinesResponse == null) {
                    headlinesResponse = resultRespnse
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newsArticles = resultRespnse.articles
                    oldArticles?.addAll(newsArticles)
                }
                Log.d("NewsViewModel", "Headlines Fetched Successfully: ${headlinesResponse?.articles?.size} articles")

                return Resource.Success(headlinesResponse ?: resultRespnse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultRespnse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultRespnse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultRespnse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultRespnse)


            }

        }
        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticles(article)
    }

    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }

    }

    private suspend fun headlinesInternet(countryCode: String) {
        headlines.postValue(Resource.Loading())
        Log.d("TEST", "Starting network request for headlines")

        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                Log.d("TEST", "Network request completed")
                Log.d("TEST", "Response code: ${response.code()}, message: ${response.message()}")

                headlines.postValue(handleHeadlinesResponse(response))

            } else {
                Log.e("TEST", "No Internet Connection")

                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            Log.e("TEST", "Exception during network request: ${t.message}")

            when (t) {
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                Log.d("NewsViewModel", "Search response code: ${response.code()}, message: ${response.message()}")

                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable) {
            Log.e("NewsViewModel", "Exception during search: ${t.localizedMessage}", t)
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("No signal"))
            }
        }


    }

}