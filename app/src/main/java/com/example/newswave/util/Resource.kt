package com.example.newswave.util


sealed class Resource <T>(
    val data:T? = null,
    val messenger:String?=null

){
    class Success<T>(data: T):Resource<T>(data)
    class Error<T>(messenger: String,data: T? = null):Resource<T>(data, messenger)
    class Loading<T>:Resource<T>()
}