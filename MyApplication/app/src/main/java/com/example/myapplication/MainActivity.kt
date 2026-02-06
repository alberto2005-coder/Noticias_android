package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.os.Build
import android.Manifest
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.NewsRepository
import com.example.myapplication.ui.NewsDetailFragment
import com.example.myapplication.ui.NewsListFragment
import com.example.myapplication.ui.NewsViewModel
import com.example.myapplication.ui.NewsViewModelFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            triggerNotificationWorker()
        } else {
            Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var viewModel: NewsViewModel
    private var isTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Init Dependencies
        val database = AppDatabase.getDatabase(this)
        val repository = NewsRepository(database.newsDao())
        val factory = NewsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)

        // Check for TwoPane (Tablet)
        if (findViewById<View>(R.id.fragment_container_detail) != null) {
            isTwoPane = true
        }

        if (savedInstanceState == null) {
            val listFragment = NewsListFragment()
            if (isTwoPane) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_list, listFragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_detail, NewsDetailFragment())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, listFragment)
                    .commit()
            }
        }

        // Observe Selection to handle navigation
        viewModel.selectedNews.observe(this) { news ->
            if (news != null) {
                if (!isTwoPane) {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, NewsDetailFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        
        viewModel.refresh()
        setupWorker()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) viewModel.search(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) viewModel.search(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_date -> {
                viewModel.loadNewsSortedByDate()
                true
            }
            R.id.action_sort_importance -> {
                viewModel.loadNewsSortedByImportance()
                true
            }
            R.id.action_benchmark -> {
                runBenchmark()
                true
            }
            R.id.action_test_notification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        triggerNotificationWorker()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    triggerNotificationWorker()
                }
                true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun runBenchmark() {
        AsyncBenchmark.showBenchmarkDialog(this)
    }

    private fun showSettingsDialog() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        val interval = prefs.getLong("notification_interval", 15) // Minutes

        val intervals = arrayOf("15 Min", "30 Min", "60 Min")
        val intervalValues = longArrayOf(15, 30, 60)
        var selectedIntervalIndex = 0
        when (interval) {
            30L -> selectedIntervalIndex = 1
            60L -> selectedIntervalIndex = 2
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Settings")
        
        // Custom simple layout or multi-choice? Simple for now.
        // We'll show a toggle and a list? Use checks.
        // Actually, just a simple "Enable Notifications" checkbox and a separate dialog for interval? 
        // Or a multi-choice single dialog.
        // Let's make it simpler: Toggle Notifications. If enabled, ask interval.
        
        // Let's use two dialogs or one custom one. Custom view is better. 
        // Or standard setMultiChoiceItems. 
        // Let's stick to "Notifications: On/Off" for simplicity and "Interval" sub-menu if needed.
        // Or just "Configure Notifications".
        
        builder.setMultiChoiceItems(
            arrayOf("Enable Notifications"), 
            booleanArrayOf(enabled)
        ) { _, _, isChecked ->
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            setupWorker()
        }
        
        builder.setPositiveButton("Set Interval") { _, _ ->
             // Show interval picker
             AlertDialog.Builder(this)
                 .setTitle("Select Interval")
                 .setSingleChoiceItems(intervals, selectedIntervalIndex) { d, which ->
                     prefs.edit().putLong("notification_interval", intervalValues[which]).apply()
                     setupWorker()
                     d.dismiss()
                 }
                 .show()
        }

        builder.setNeutralButton("Language") { _, _ ->
            showLanguageSelection()
        }
        
        builder.setNegativeButton("Close", null)
        builder.show()
    }

    private fun showLanguageSelection() {
        val languages = arrayOf("Español", "English", "Français")
        val codes = arrayOf("es", "en", "fr")
        
        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val code = codes[which]
                val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(code)
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
            }
            .show()
    }

    private fun setupWorker() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        val interval = prefs.getLong("notification_interval", 15)

        val workManager = WorkManager.getInstance(this)
        
        if (enabled) {
            val workRequest = PeriodicWorkRequestBuilder<NewsWorker>(interval, TimeUnit.MINUTES)
                .build()
            
            workManager.enqueueUniquePeriodicWork(
                "NewsUpdateWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } else {
            workManager.cancelUniqueWork("NewsUpdateWork")
        }
    }

    private fun triggerNotificationWorker() {
        val workRequest = androidx.work.OneTimeWorkRequest.Builder(com.example.myapplication.NewsWorker::class.java).build()
        androidx.work.WorkManager.getInstance(this).enqueue(workRequest)
        Toast.makeText(this, "Notification triggered", Toast.LENGTH_SHORT).show()
    }
}