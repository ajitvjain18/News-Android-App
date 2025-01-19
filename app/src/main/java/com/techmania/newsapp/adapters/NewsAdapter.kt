package com.techmania.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techmania.newsapp.R
import com.techmania.newsapp.databinding.LayoutNewsItemBinding
import com.techmania.newsapp.retrofit.Article

class NewsAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onDeleteClick: (String) -> Unit
) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var newsList = mutableListOf<Article>()
    private var isOfflineMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val binding =
            LayoutNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount() = newsList.size

    fun updateList(list: List<Article>, isOffline: Boolean) {
        newsList.clear()
        newsList.addAll(list)
        isOfflineMode = isOffline
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Article) {
            binding.tvHeading.text = item.title
            binding.tvDescription.text = item.description

            Glide.with(binding.root.context).load(item.urlToImage)
                .error(R.drawable.error_image)
                .into(binding.ivNews)

            binding.readMoreButton.setOnClickListener {
                onItemClick(item)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClick(item.url)
            }
            if (!isOfflineMode) {
                binding.deleteButton.isVisible = false
            } else {
                binding.deleteButton.isVisible = true
            }
        }
    }
}