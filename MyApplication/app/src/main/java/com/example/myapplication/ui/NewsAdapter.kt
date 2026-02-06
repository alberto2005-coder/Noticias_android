package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ImageDownloader
import com.example.myapplication.R
import com.example.myapplication.data.NewsEntity
import coil.load
import java.text.DateFormat

class NewsAdapter(private val onItemClick: (NewsEntity) -> Unit) : 
    ListAdapter<NewsEntity, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
        // Only animate if position > lastPosition to avoid repeated animations on scroll up
        if (position > lastPosition) {
            setFadeAnimation(holder.itemView)
            lastPosition = position
        }
    }

    private var lastPosition = -1

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        view.startAnimation(anim)
    }

    class NewsViewHolder(itemView: View, val onItemClick: (NewsEntity) -> Unit) : 
        RecyclerView.ViewHolder(itemView) {
        
        private val title: TextView = itemView.findViewById(R.id.txtTitle)
        private val desc: TextView = itemView.findViewById(R.id.txtDescription)
        private val date: TextView = itemView.findViewById(R.id.txtDate)
        private val importance: TextView = itemView.findViewById(R.id.txtImportance)
        private val image: ImageView = itemView.findViewById(R.id.imgThumbnail)

        fun bind(news: NewsEntity) {
            title.text = news.title
            desc.text = news.shortDescription
            date.text = DateFormat.getDateInstance().format(news.publishedDate)
            importance.text = "Imp: ${news.importance}"
            
            // Use Coil for efficient image loading
             if (news.thumbnailUrl.isNotEmpty()) {
                image.load(news.thumbnailUrl) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_delete)
                }
            } else {
                image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            
            itemView.setOnClickListener { onItemClick(news) }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<NewsEntity>() {
        override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
            return oldItem == newItem
        }
    }
}
