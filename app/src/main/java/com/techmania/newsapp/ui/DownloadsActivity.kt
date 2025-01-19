package com.techmania.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.techmania.newsapp.R
import com.techmania.newsapp.adapters.NewsAdapter
import com.techmania.newsapp.databinding.LayoutDownloadsActivityBinding
import com.techmania.newsapp.room.NewsApplication
import com.techmania.newsapp.viewmodels.HomeViewModel
import com.techmania.newsapp.viewmodels.HomeViewModelFactory

class DownloadsActivity : BaseActivity() {

    private lateinit var binding: LayoutDownloadsActivityBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var selectedActivity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDownloadsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = HomeViewModelFactory((application as NewsApplication).repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        selectedActivity = binding.buttonsLayout.downloadButton
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.home_sale_text_green))
        binding.buttonsLayout.homeButton.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_unselected_title
            )
        )
        viewModel.loadOffline()
        initClicks()
        initObservers()
        initAdapter()
        initBaseVm(viewModel)
    }

    private fun initClicks() {
        with(binding.buttonsLayout) {
            homeButton.setOnClickListener { setSelectedView(it) }
            downloadButton.setOnClickListener { setSelectedView(it) }
        }
        with(binding) {
            backIv.setOnClickListener { finish() }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            offlinenewsLiveData.observe(this@DownloadsActivity) {

                if (it.isEmpty()) {
                    binding.titleTv.text = "Saved Articles" + "(${it.size})"
                    binding.empty.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.VISIBLE
                    binding.itemsRV.visibility = View.GONE
                } else {
                    binding.empty.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    binding.itemsRV.visibility = View.VISIBLE
                    binding.titleTv.text = "Saved Articles" + "(${it.size})"
                    adapter.updateList(it, true)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = NewsAdapter(onItemClick = {
            viewModel.storeOfflineArticle(it)
            val fragment = DownloadFragment()
            addFragment(binding.container.id, fragment)
        }, onDeleteClick = {
            viewModel.deleteArticelFromLocal(it)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        })
        binding.itemsRV.adapter = adapter
    }

    private fun setSelectedView(view: View) {
        if (selectedActivity == view) return
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.color_unselected_title))
        selectedActivity = view as TextView
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.home_sale_text_green))
        if (selectedActivity.text.equals("Downloads")) {
            return
        } else {
            if (selectedActivity.text.equals("Home")) {
                val intent = Intent(this@DownloadsActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}