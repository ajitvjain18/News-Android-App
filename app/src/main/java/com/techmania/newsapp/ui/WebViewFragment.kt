package com.techmania.newsapp.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.techmania.newsapp.databinding.LayoutWebViewBinding
import com.techmania.newsapp.room.NewsApplication
import com.techmania.newsapp.viewmodels.HomeViewModel
import com.techmania.newsapp.viewmodels.HomeViewModelFactory

class WebViewFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var binding: LayoutWebViewBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var receivedString: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receivedString = requireArguments().getString(URL).toString()
        initWebView(receivedString)
        val factory = HomeViewModelFactory(
            (requireActivity().application as NewsApplication).repository,
            requireActivity().application
        )
        viewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
        initClicks()
    }

    private fun initClicks() {
        binding.download.setOnClickListener {
            viewModel.saveArticle()
            Toast.makeText(requireContext(), "Article Downloaded", Toast.LENGTH_SHORT).show()
        }
        binding.share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, receivedString)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    private fun initWebView(url: String?) {
        webView = binding.webView
        webView.settings.javaScriptEnabled = false
        webView.settings.setSupportZoom(true)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: android.graphics.Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = android.view.View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
        if (url != null) {
            webView.loadUrl(url)
        }
    }

    companion object {

        private const val URL = "URL"

        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }
}