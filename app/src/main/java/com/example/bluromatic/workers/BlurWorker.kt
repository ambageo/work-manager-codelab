package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DEFAULT_BLUR_LEVEL
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, DEFAULT_BLUR_LEVEL)

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

            require(!resourceUri.isNullOrBlank()){
                val errorMessage = applicationContext.resources.getString(R.string.invalid_input_uri)
                Log.e(TAG, errorMessage)
                errorMessage
            }
            // We need a content resolver to read the contents pointed to by the Uri
            val resolver = applicationContext.contentResolver


            try {
               /* val picture = BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.android_cupcake
                )*/

                // Decode (read) the Uri to get the picture
                val picture = BitmapFactory.decodeStream(resolver.openInputStream(resourceUri.toUri()))
                val blurredPicture = blurBitmap(picture, blurLevel)

                // Create a new Uri of the blurred picture and store it
                val blurredUri = writeBitmapToFile(applicationContext, blurredPicture)

                // Create an output data object to pass it to the result
                val outputData = workDataOf(KEY_IMAGE_URI to blurredUri.toString())
                Result.success(outputData)
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
