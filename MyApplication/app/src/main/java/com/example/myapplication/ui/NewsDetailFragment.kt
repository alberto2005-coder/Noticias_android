package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.myapplication.R

class NewsDetailFragment : Fragment(R.layout.fragment_news_detail) {

    private lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        val imgDetail = view.findViewById<ImageView>(R.id.imgDetail)
        val txtTitle = view.findViewById<TextView>(R.id.txtDetailTitle)
        val txtContent = view.findViewById<TextView>(R.id.txtDetailContent)

        viewModel.selectedNews.observe(viewLifecycleOwner) { news ->
            if (news != null) {
                txtTitle.text = news.title
                txtContent.text = news.content
                if (news.largeImageUrl.isNotEmpty()) {
                    imgDetail.load(news.largeImageUrl)
                }
            }
        }
    }
}
