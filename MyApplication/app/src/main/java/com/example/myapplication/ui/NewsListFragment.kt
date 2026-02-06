package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class NewsListFragment : Fragment(R.layout.fragment_news_list) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        adapter = NewsAdapter { news ->
            viewModel.selectNews(news)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.newsList.observe(viewLifecycleOwner) { news ->
            adapter.submitList(news)
            progressBar.visibility = if (news.isEmpty()) View.VISIBLE else View.GONE
            if (news.isNotEmpty()) progressBar.visibility = View.GONE
        }
        
        // Initial load trigger if empty? ViewModel init does it.
    }
}
