package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.IMAGE_TITLE
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "SaveImageToFileWorker"

class SaveImageToFileWorker (private val context: Context, params: WorkerParameters
):CoroutineWorker(context, params) {
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )
    override suspend fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, even on emulated devices
        makeStatusNotification(
            applicationContext.resources.getString(R.string.saving_image),
            applicationContext
        )

        return withContext(Dispatchers.IO){
            delay(DELAY_TIME_MILLIS)

            // We need a content resolver to read the contents pointed to by the Uri
            val resolver = applicationContext.contentResolver

            try {
                // Get the image Uri
                val uriString = inputData.getString(KEY_IMAGE_URI)
                // Get the bitmap of it
                val bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(uriString))
                )
                // Insert an image and create a thumbnail of it
                val imageUrl = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, IMAGE_TITLE, dateFormatter.format(Date())
                )
                if(!imageUrl.isNullOrEmpty()){
                   val outputData = workDataOf(KEY_IMAGE_URI to imageUrl)
                    Result.success(outputData)
                } else {
                    Log.e(
                        TAG,
                        applicationContext.resources.getString(R.string.writing_to_mediaStore_failed)
                    )
                    Result.failure()
                }

            } catch(exception: Exception){
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_saving_image),
                    exception
                )
                Result.failure()
            }
        }
    }
}