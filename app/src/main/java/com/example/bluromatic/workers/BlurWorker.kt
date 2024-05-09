package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            context
        )
        // CoroutineWorker by default uses Dispatchers.Default - since we will write the
        // blurred image to the memory, we should use Dispatchers.IO
        // we change Dispatcher by using withContext
        return withContext(Dispatchers.IO) {

            // adding a delay just to be able to see the different notifications.
            delay(DELAY_TIME_MILLIS)

            try {
                val picture = BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.android_cupcake
                )
                val blurredPicture = blurBitmap(picture, 1)
                val blurredUri = writeBitmapToFile(applicationContext, blurredPicture)
                makeStatusNotification("Output is $blurredUri", applicationContext)
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )
                Result.failure()
            }
        }
    }
}
