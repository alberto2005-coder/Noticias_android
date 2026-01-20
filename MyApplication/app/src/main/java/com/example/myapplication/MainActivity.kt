package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
        val viewTypes = arrayOf("ListView", "GridView", "StackView", "Gallery")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, viewTypes)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                switchView(viewTypes[position])
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
                val currentViewType = spinner.selectedItem as? String
                if (currentViewType != null) {
                    switchView(currentViewType)
                }
            } else {
                Toast.makeText(this, "Failed to load data or no data found", Toast.LENGTH_LONG).show()
                dataItems = listOf(
                    NewsItem("Demo Title 1", "Short Desc 1", "", "Content 1", ""),
                    NewsItem("Demo Title 2", "Short Desc 2", "", "Content 2", ""),
                    NewsItem("Demo Title 3", "Short Desc 3", "", "Content 3", ""),
                    NewsItem("Demo Title 4", "Short Desc 4", "", "Content 4", "")
                )
                 val currentViewType = spinner.selectedItem as? String
                if (currentViewType != null) {
                    switchView(currentViewType)
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
            val item = dataItems[position]
            startActivity(DetailActivity.newIntent(this, item))
        }

        container.addView(adapterView)
    }
}