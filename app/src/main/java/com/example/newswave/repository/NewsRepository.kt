package com.example.newswave.repository

import com.example.newswave.api.RetrofitInstance
import com.example.newswave.db.ArticleDatabase
import com.example.newswave.models.Article

class NewsRepository(val db:ArticleDatabase) {
    suspend fun getHeadlines(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)
    suspend fun searchNews(searchQuery:String,pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
    suspend fun upsert(article: Article)= db.getArticleDao().upsert(article)
    fun getFavouriteNews() = db.getArticleDao().getAllArticles()
    suspend fun deleteArticles(article: Article) = db.getArticleDao().deleteArticles(article)
}