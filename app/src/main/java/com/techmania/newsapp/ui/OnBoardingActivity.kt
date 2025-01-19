package com.techmania.newsapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.CompositePageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.techmania.newsapp.adapters.ViewPagerAdapter
import com.techmania.newsapp.databinding.LayoutOnboardingActivityBinding

class OnBoardingActivity : BaseActivity() {

    private lateinit var binding: LayoutOnboardingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutOnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPager()
        initClicks()
    }

    private fun initClicks() {
        binding.tvSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setPager() {
        val pager = binding.sliderPager
        val tabLayout = binding.tabLayout
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        val compositePageTransformer = CompositePageTransformer()
        pager.clipToPadding = false
        pager.clipChildren = false
        pager.offscreenPageLimit = 2
        pager.setPageTransformer(compositePageTransformer)
        pager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, pager) { tab, position ->
        }.attach()

        binding.next.setOnClickListener {
            val currentPage = pager.currentItem
            val lastPage = pagerAdapter.itemCount - 1

            if (currentPage == lastPage) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                pager.setCurrentItem(currentPage + 1, true)
            }
        }
    }
}