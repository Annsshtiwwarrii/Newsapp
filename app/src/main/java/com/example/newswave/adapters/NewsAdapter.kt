package com.example.newswave.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsprojectpractice.R
import com.example.newswave.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewholder>() {
    inner class ArticleViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    lateinit var articleImage: ImageView
    lateinit var articleSource: TextView
    lateinit var articleTitle: TextView
    lateinit var articleDescription: TextView
    lateinit var articleDateTime: TextView
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewholder {
        return ArticleViewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
private var onItemClickListner:((Article)-> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewholder, position: Int) {
val article = differ.currentList[position]
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)
        holder.itemView.apply {
            val imageurl = article.urlToImage
            if (!imageurl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageurl)
                    .into(articleImage)}
            else {
                articleImage.setImageResource(R.drawable.news)
            }
                Log.d("news", "image is ${articleImage}")

                articleSource.text = article.source!!.name
                articleTitle.text = article.title
                articleDescription.text = article.description
                articleDateTime.text = article.publishedAt
                setOnClickListener {
                    onItemClickListner?.let {
                        it(article)
                    }
                }

            }
        }

    fun setOnItemClickListner(listner:(Article)->Unit){
        onItemClickListner = listner
    }



}
