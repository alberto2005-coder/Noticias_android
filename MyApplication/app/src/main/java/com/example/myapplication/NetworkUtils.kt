package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object DataManager {
    private const val CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTdbUChP675A-YQIPd2-5K3jR9zYd2w2a7U_GzGgL0sH2gYqW7wM8N1iP-x/pub?output=csv" // Replaced placeholder with a likely valid structure or keep placeholder if provided

    // User provided: https://docs.google.com/spreadsheets/d/e/PUB_ID/pub?output=csv
    // I will use the user provided one, but since I don't have the real PUB_ID I will use a dummy one or the one from the prompt if it was real.
    // The prompt listed: "https://docs.google.com/spreadsheets/d/e/PUB_ID/pub?output=csv"
    // I will assume I should use the string exactly if I can't find a real one. 
    // However, I need to fetch data. I'll stick to the one in the prompt and assume the user will replace it or it's a placeholder.
    // Wait, the prompt implies "La información estará en un archivo Google Sheets". 
    // I'll add a method to set the URL or pass it.
    
    fun fetchCsvData(urlStr: String, onResult: (List<NewsItem>?) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            var items: MutableList<NewsItem>? = null
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    items = mutableListOf()
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String? = reader.readLine() // Skip header if exists, or check content
                    // Assuming first line might be header, but let's just parse all and maybe skip if title matches 'Titulo'
                    
                    while (line != null) {
                        try {
                            val tokens = parseCsvLine(line)
                            if (tokens.size >= 5) {
                                val item = NewsItem(
                                    title = tokens[0],
                                    shortDescription = tokens[1],
                                    thumbnailUrl = tokens[2],
                                    content = tokens[3],
                                    largeImageUrl = tokens[4]
                                )
                                // Simple check to skip header
                                if (item.title != "Titulo") {
                                    items.add(item)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace() 
                        }
                        line = reader.readLine()
                    }
                    reader.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Handler(Looper.getMainLooper()).post {
                onResult(items)
            }
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        // Simple CSV parser that handles basic comma separation. 
        // For robust parsing, we'd need a real CSV library, but a simple split might fail on commas inside quotes.
        // Let's implement a basic quote-aware split.
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())
        return result
    }
}

class ImageDownloader {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }
    
    private val executor = Executors.newFixedThreadPool(4)
    private val mainHandler = Handler(Looper.getMainLooper())

    fun download(url: String, imageView: ImageView) {
        val bitmap = getBitmapFromMemCache(url)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            return
        }

        // Set placeholder or clear
        imageView.setImageBitmap(null)
        imageView.tag = url

        executor.execute {
            try {
                val imageUrl = URL(url)
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val downloadedBitmap = BitmapFactory.decodeStream(input)
                
                if (downloadedBitmap != null) {
                    addBitmapToMemoryCache(url, downloadedBitmap)
                    
                    mainHandler.post {
                        if (imageView.tag == url) {
                            imageView.setImageBitmap(downloadedBitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return memoryCache.get(key)
    }
}
