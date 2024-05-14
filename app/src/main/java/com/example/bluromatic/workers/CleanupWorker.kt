package com.example.bluromatic.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.OUTPUT_PATH
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "CleanupWorker"

/**
 * Cleans up temporary files generated during blurring process
 */
class CleanupWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        /** Makes a notification when the work starts and slows down the work so that it's easier
         * to see each WorkRequest start, even on emulated devices
         */
        makeStatusNotification(
            applicationContext.resources.getString(R.string.cleaning_up_files),
            applicationContext
        )
        return withContext(Dispatchers.IO) {
            delay(DELAY_TIME_MILLIS)

            try {
                // Create a File object representing the directory where the output files exist
                // .filesDir is the absolute (parent) path where the output files are created
                // OUTPUT_PATH is the relative (child) path where we store the (temp) bitmap
                // of the blurred image. This is done in the writeBitmapToFile fun in WorkerUtils
                val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
                if (outputDirectory.exists()) {
                    val entries = outputDirectory.listFiles()
                    if (entries != null) {
                        for (entry in entries) {
                            val name = entry.name
                            // We are looking for names ending in ".png" because that's what the
                            // blurred images end with (again, look at writeBitmapToFile)
                            if (name.isNotEmpty() && name.endsWith(".png")) {
                                val deleted = entry.delete()
                                Log.i(TAG, "Deleted $name - $deleted")
                            }
                        }
                    }
                }
                Result.success()
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_cleaning_file),
                    exception
                )
                Result.failure()
            }
        }
    }
}