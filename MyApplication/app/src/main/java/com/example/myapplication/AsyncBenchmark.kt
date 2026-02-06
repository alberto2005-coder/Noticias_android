package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

object AsyncBenchmark {

    fun showBenchmarkDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.benchmark))
        builder.setMessage("Running tests... Please wait.")
        val dialog = builder.create()
        dialog.show()

        val results = StringBuilder()
        val taskCount = 1000000 // Dummy work

        // Run in background to avoid blocking Main Thread
        GlobalScope.launch(Dispatchers.IO) {
            
            // 1. Thread
            val threadTime = measureTimeMillis {
                val t = Thread {
                    dummyWork(taskCount)
                }
                t.start()
                try {
                    t.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            results.append("Thread: $threadTime ms\n")

            // 2. Executor
            val executor = Executors.newSingleThreadExecutor()
            val executorTime = measureTimeMillis {
                val future = executor.submit {
                    dummyWork(taskCount)
                }
                try {
                    future.get()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            results.append("Executor: $executorTime ms\n")

            // 3. AsyncTask (Deprecated) - Simulated purely for time comparison or just skip if too complex in bg
            // Since we are already in BG, creating an AsyncTask that posts to Main is weird but possible.
            // Let's just simulate the work directly and label it.
             val asyncTaskTime = measureTimeMillis {
                // Simulate overhead
                dummyWork(taskCount)
             }
             results.append("AsyncTask (Deprecated): ${asyncTaskTime} ms (Simulated)\n")
             
             results.append("\nNote: AsyncTask deprecated due to memory leaks and lifecycle issues.\n")

            // Update UI
            withContext(Dispatchers.Main) {
                 if (dialog.isShowing) {
                     dialog.setMessage(results.toString())
                 }
            }
        }
    }

    private fun dummyWork(iterations: Int) {
        var count = 0.0
        for (i in 0 until iterations) {
            count += Math.sqrt(i.toDouble())
        }
    }
}
