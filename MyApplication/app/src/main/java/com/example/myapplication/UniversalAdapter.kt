package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class UniversalAdapter(
    private val context: Context,
    private var items: List<NewsItem>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val imageDownloader = ImageDownloader()

    fun updateData(newItems: List<NewsItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_view, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.txtTitle),
                view.findViewById(R.id.txtDescription),
                view.findViewById(R.id.imgThumbnail)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = items[position]
        holder.title.text = item.title
        holder.description.text = item.shortDescription
        
        // Reset image and load async
        holder.thumbnail.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder
        if (item.thumbnailUrl.isNotEmpty()) {
            imageDownloader.download(item.thumbnailUrl, holder.thumbnail)
        }

        return view
    }

    private class ViewHolder(
        val title: TextView,
        val description: TextView,
        val thumbnail: ImageView
    )
}
