package com.example.myapplication

import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object DataManager {
    // Replaced placeholder with a likely valid structure or keep placeholder if provided
    private const val CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTdbUChP675A-YQIPd2-5K3jR9zYd2w2a7U_GzGgL0sH2gYqW7wM8N1iP-x/pub?output=csv" 

    var currentItems: List<NewsItem>? = null

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
                    val fullContent = reader.readText()
                    reader.close()

                    val rows = parseCsv(fullContent)
                    
                    for (tokens in rows) {
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
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Cache the items
            if (items != null) {
                currentItems = items
            }

            Handler(Looper.getMainLooper()).post {
                onResult(items)
            }
        }
    }

    private fun parseCsv(content: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val currentLine = mutableListOf<String>()
        var currentToken = StringBuilder()
        var inQuotes = false
        
        for (i in content.indices) {
            val char = content[i]
            val nextChar = if (i + 1 < content.length) content[i + 1] else null

            if (inQuotes) {
                if (char == '"') {
                    if (nextChar == '"') {
                        // Escaped quote
                        currentToken.append('"')
                        // Skip next char in loop ? No, simplest way for loop not to skip is to handle here but we can rely on standard csv logic. 
                        // Actually standard CSV: "" is an escaped quote within quotes.
                        // But my loop iterates one by one. I need to advance index if I consume nextChar. 
                        // Easier: handle it by tracking previous or just let the loop run.
                        // Let's stick to standard state machine:
                    } else {
                        inQuotes = false
                    }
                } else {
                    currentToken.append(char)
                }
            } else {
                when (char) {
                    '"' -> inQuotes = true
                    ',' -> {
                        currentLine.add(currentToken.toString().trim())
                        currentToken = StringBuilder()
                    }
                    '\n', '\r' -> {
                        // Handle standard line breaks
                        // Check if it's \r\n, consume \n if so?
                        // The loop processes \r then \n.
                        // If \r, ends line. If next is \n, it will be processed next iteration as empty line end?
                        // Better to be specific about line endings.
                        
                        if (char == '\r' && nextChar == '\n') {
                            // Skip this char, let \n handle the break
                        } else {
                             // End of line
                             // Only add if we have content (avoid empty lines from \r\n or trailing newlines)
                             currentLine.add(currentToken.toString().trim())
                             result.add(ArrayList(currentLine)) // Copy
                             currentLine.clear()
                             currentToken = StringBuilder()
                        }
                    }
                    else -> currentToken.append(char)
                }
            }
        }
        
        // Add last token/line if exists
        if (currentToken.isNotEmpty() || currentLine.isNotEmpty()) {
             currentLine.add(currentToken.toString().trim())
             result.add(currentLine)
        }
        
        return result
    }
}