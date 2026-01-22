package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Gallery
import android.widget.GridView
import android.widget.ListView
import android.widget.Spinner
import android.widget.StackView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var container: FrameLayout
    private lateinit var spinner: Spinner
    private var dataItems: List<NewsItem> = emptyList()
    private var currentAdapter: UniversalAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.container)
        spinner = findViewById(R.id.spinnerViewType)

        setupSpinner()
        loadData()
    }

    private fun setupSpinner() {
        val viewTypes = listOf(
            NewsItem("ListView", "Standard list view", "", "", ""),
            NewsItem("GridView", "Grid layout (2 columns)", "", "", ""),
            NewsItem("StackView", "Stack card view", "", "", ""),
            NewsItem("Gallery", "Horizontal gallery", "", "", "")
        )
        
        val adapter = UniversalAdapter(this, viewTypes)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                switchView(viewTypes[position].title)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadData() {
        val url = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTdbUChP675A-YQIPd2-5K3jR9zYd2w2a7U_GzGgL0sH2gYqW7wM8N1iP-x/pub?output=csv"
        val targetUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-1vQ6mL-J0ec_25uVCXUgdhIxyiGa1ctbtKKxyGo5aWhV3n-ySyO00ecSicMuiohkKI2pqikIHUZAhqNz/pub?output=csv"

        DataManager.fetchCsvData(targetUrl) { items ->
            if (items != null && items.isNotEmpty()) {
                dataItems = items
                val selectedItem = spinner.selectedItem as? NewsItem
                if (selectedItem != null) {
                    switchView(selectedItem.title)
                }
            } else {
                Toast.makeText(this, "Failed to load data or no data found", Toast.LENGTH_LONG).show()
                dataItems = listOf(
                    NewsItem("Demo Title 1", "Short Desc 1", "", "Content 1", ""),
                    NewsItem("Demo Title 2", "Short Desc 2", "", "Content 2", ""),
                    NewsItem("Demo Title 3", "Short Desc 3", "", "Content 3", ""),
                    NewsItem("Demo Title 4", "Short Desc 4", "", "Content 4", "")
                )
                 val selectedItem = spinner.selectedItem as? NewsItem
                if (selectedItem != null) {
                    switchView(selectedItem.title)
                }
            }
        }
    }

    private fun switchView(type: String) {
        container.removeAllViews()

        val adapterView: AdapterView<UniversalAdapter> = when (type) {
            "ListView" -> ListView(this)
            "GridView" -> {
                val grid = GridView(this)
                grid.numColumns = 2
                grid.verticalSpacing = 16
                grid.horizontalSpacing = 16
                grid
            }
            "StackView" -> StackView(this)
            "Gallery" -> {
                val gallery = Gallery(this)
                gallery.setSpacing(16)
                gallery
            }
            else -> ListView(this)
        } as AdapterView<UniversalAdapter>

        adapterView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        currentAdapter = UniversalAdapter(this, dataItems)
        adapterView.adapter = currentAdapter
        
        adapterView.setOnItemClickListener { _, _, position, _ ->
            // Pass the index instead of the item to avoid TransactionTooLargeException
            startActivity(DetailActivity.newIntent(this, position))
        }

        container.addView(adapterView)
    }
}