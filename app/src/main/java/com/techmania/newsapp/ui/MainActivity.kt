package com.techmania.newsapp.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.techmania.newsapp.R
import com.techmania.newsapp.adapters.NewsAdapter
import com.techmania.newsapp.databinding.LayoutMainActivityBinding
import com.techmania.newsapp.room.NewsApplication
import com.techmania.newsapp.viewmodels.HomeViewModel
import com.techmania.newsapp.viewmodels.HomeViewModelFactory

class MainActivity : BaseActivity() {

    private lateinit var binding: LayoutMainActivityBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var selectedCategory: TextView
    private lateinit var selectedActivity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = HomeViewModelFactory((application as NewsApplication).repository, application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val isOnline = isNetworkAvailable()
        binding.itemsRV.isVisible = isOnline
        binding.offline.isVisible = !isOnline
        binding.offlineText.isVisible = !isOnline

        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    binding.itemsRV.isVisible = true
                    binding.offline.isVisible = false
                    binding.offlineText.isVisible = false
                    viewModel.loadNews("all")
                }
            }
            override fun onLost(network: Network) {
                runOnUiThread {
                    binding.itemsRV.isVisible = false
                    binding.offline.isVisible = true
                    binding.offlineText.isVisible = true
                }
            }
        }
        registerNetworkCallback()

        selectedCategory = binding.categoryLayout.home
        selectedActivity = binding.buttonsLayout.homeButton

        initClicks()
        initObservers()
        initAdapter()
        initBaseVm(viewModel)
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onStart() {
        super.onStart()
        selectedActivity = binding.buttonsLayout.homeButton
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.home_sale_text_green))
        binding.buttonsLayout.downloadButton.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_unselected_title
            )
        )
        checkAndRequestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun initClicks() {
        with(binding.categoryLayout) {
            home.setOnClickListener { setSelectedView(it) }
            india.setOnClickListener { setSelectedView(it) }
            world.setOnClickListener { setSelectedView(it) }
            science.setOnClickListener { setSelectedView(it) }
            business.setOnClickListener { setSelectedView(it) }
            technology.setOnClickListener { setSelectedView(it) }
            entertainment.setOnClickListener { setSelectedView(it) }
            sports.setOnClickListener { setSelectedView(it) }
            health.setOnClickListener { setSelectedView(it) }
        }
        with(binding.buttonsLayout) {
            homeButton.setOnClickListener { selectedActivity(it) }
            downloadButton.setOnClickListener { selectedActivity(it) }
        }
    }

    private fun selectedActivity(view: View) {
        if (selectedActivity == view) return
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.color_unselected_title))
        selectedActivity = view as TextView
        selectedActivity.setTextColor(ContextCompat.getColor(this, R.color.home_sale_text_green))
        if (selectedActivity.text.equals("Home")) {
            return
        } else {
            if (selectedActivity.text.equals("Downloads")) {
                val intent = Intent(this@MainActivity, DownloadsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setSelectedView(view: View) {
        if (selectedCategory == view) return
        selectedCategory.setTextColor(ContextCompat.getColor(this, R.color.color_unselected_title))
        selectedCategory = view as TextView
        selectedCategory.setTextColor(ContextCompat.getColor(this, R.color.home_sale_text_green))
        viewModel.loadNews(selectedCategory.text.toString())
    }

    private fun initObservers() {
        with(viewModel) {
            newsLiveData.observe(this@MainActivity) {
                adapter.updateList(it, false)
            }
        }
    }

    private fun initAdapter() {
        adapter = NewsAdapter(onItemClick = {
            viewModel.storeArticle(it)
            val fragment = WebViewFragment.newInstance(it.url)
            addFragment(binding.container.id, fragment)
        }, onDeleteClick = {

        })
        binding.itemsRV.adapter = adapter
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}
