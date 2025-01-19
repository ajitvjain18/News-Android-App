package com.techmania.newsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techmania.newsapp.ui.FirstPageFragment
import com.techmania.newsapp.ui.SecondPageFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val pageCount = 2
    override fun getItemCount(): Int {
        return pageCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstPageFragment()
            1 -> SecondPageFragment()
            else -> null
        }!!
    }

}
