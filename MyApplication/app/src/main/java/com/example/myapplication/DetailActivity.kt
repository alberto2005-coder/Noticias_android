package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class DetailActivity : Activity() {

    companion object {
        private const val EXTRA_ITEM = "extra_item"

        fun newIntent(context: Context, item: NewsItem): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_ITEM, item)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val item = intent.getSerializableExtra(EXTRA_ITEM) as? NewsItem
        
        if (item != null) {
            val titleView = findViewById<TextView>(R.id.txtDetailTitle)
            val contentView = findViewById<TextView>(R.id.txtDetailContent)
            val imageView = findViewById<ImageView>(R.id.imgDetail)

            titleView.text = item.title
            contentView.text = item.content
            
            if (item.largeImageUrl.isNotEmpty()) {
                ImageDownloader().download(item.largeImageUrl, imageView)
            }
        }
    }
}
