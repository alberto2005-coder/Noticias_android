package com.example.myapplication

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Implementation of App Widget functionality for News.
 */
class NewsWidget : AppWidgetProvider() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        // Handle widget update action
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
             val pendingResult = goAsync()
             scope.launch(Dispatchers.IO) {
                 try {
                     val appWidgetManager = AppWidgetManager.getInstance(context)
                     val ids = appWidgetManager.getAppWidgetIds(
                         ComponentName(context, NewsWidget::class.java)
                     )
                     updateNewsWidgets(context, appWidgetManager, ids)
                 } finally {
                     pendingResult.finish()
                 }
             }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // We handle updates in onReceive to support async correctly for database access
        // But for standard flow, we trigger the same logic if needed, though onReceive covers it.
        // Leaving empty or directing to onReceive logic via intent if needed.
    }

    private suspend fun updateNewsWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val database = AppDatabase.getDatabase(context)
        val repository = NewsRepository(database.newsDao())

        val latestNewsList = repository.allNewsDate.firstOrNull()
        val latestNews = latestNewsList?.firstOrNull()

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_news)

            // Open MainActivity when clicked
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_news_title, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

            if (latestNews != null) {
                views.setTextViewText(R.id.widget_news_title, latestNews.title)
                
                val dateString = try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    sdf.format(Date(latestNews.publishedDate))
                } catch (e: Exception) {
                    ""
                }
                views.setTextViewText(R.id.widget_news_date, dateString)
            } else {
                views.setTextViewText(R.id.widget_news_title, "No hay noticias recientes")
                views.setTextViewText(R.id.widget_news_date, "")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
