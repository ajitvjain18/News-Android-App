package com.techmania.newsapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.techmania.newsapp.R
import com.techmania.newsapp.databinding.LayoutDownloadsFragmentBinding
import com.techmania.newsapp.room.NewsApplication
import com.techmania.newsapp.viewmodels.HomeViewModel
import com.techmania.newsapp.viewmodels.HomeViewModelFactory

class DownloadFragment : Fragment() {

    private lateinit var binding: LayoutDownloadsFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDownloadsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = HomeViewModelFactory(
            (requireActivity().application as NewsApplication).repository,
            requireActivity().application
        )
        viewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {

            Glide.with(this@DownloadFragment)
                .load(selectedOfflineArticle.localImagePath)
                .error(R.drawable.error_image)
                .into(binding.ivNews)

            binding.tvHeading.text = selectedOfflineArticle.title
            binding.tvDescription.text = selectedOfflineArticle.description
        }
    }


}