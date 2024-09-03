package com.example.newswave.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentArticleBinding
import com.example.newswave.models.Article
import com.example.newswave.ui.NewsActivity
import com.example.newswave.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

lateinit var newsViewModel: NewsViewModel
val args:ArticleFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article
        Log.d("Article Fragment","Article URL ${article.url}")
        binding.webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            article.url?.let {
                loadUrl(it)
            }
        }
        binding.fab.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view,"Add to Favourite",Snackbar.LENGTH_SHORT).show()
        }

    }


    }