package com.example.newswave.ui.fragments

import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentHeadlinesBinding
import com.example.newswave.adapters.NewsAdapter
import com.example.newswave.ui.NewsActivity
import com.example.newswave.ui.NewsViewModel
import com.example.newswave.util.Constants
import com.example.newswave.util.Resource


class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {
    lateinit var newsviewMode: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadlinesError: CardView
    lateinit var binding: FragmentHeadlinesBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_error, null)
        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)
        newsviewMode = (activity as NewsActivity).newsViewModel
        setUpHeadlinesRecycler()
        newsAdapter.setOnItemClickListner {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_headlinesFragment2_to_articleFragment, bundle)
        }
        newsviewMode.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success<*> -> {
                    Log.d("Headlines Fragment","Data fetched Successfully")
                    hideProgressBar()
                    hideErrorMessege()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsviewMode.headlinesPage == totalPages
                        if (isLastPage) {

                            binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.messenger?.let { messege ->
                        Log.e("HeadlinesFragment","Error occured ${messege}")
                        Toast.makeText(activity, "Sorry error:  $messege", Toast.LENGTH_LONG).show()
                        showErrorMessege(messege)
                    }

                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }

            }
        })
        retryButton.setOnClickListener {
            newsviewMode.getHeadlines("us")
        }
    }
    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessege() {
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessege(message: String) {
        itemHeadlinesError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListner = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNoError = !isError
            val isNotLoadingLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoError && isNotLoadingLastPage && isAtLastItem && isNotAtBegning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsviewMode.getHeadlines("us")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }

    private fun setUpHeadlinesRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListner)
        }
    }
}
