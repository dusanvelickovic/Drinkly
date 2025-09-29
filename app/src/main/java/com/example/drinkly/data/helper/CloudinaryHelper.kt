package com.example.drinkly.data.helper

import android.app.Application
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.drinkly.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object CloudinaryHelper {
    /**
     * Initialize Cloudinary MediaManager with configuration from BuildConfig.
     * This should be called once, ideally in the Application class.
     */
    fun init(application: Application) {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )

        MediaManager.init(application, config)
    }

    /**
     * Get the singleton instance of MediaManager.
     */
    fun getMediaManager(): MediaManager {
        return MediaManager.get()
    }

    /**
     * Upload an image to Cloudinary and return the secure URL.
     */
    suspend fun uploadImageToCloudinary(imageUri: Uri): String = suspendCancellableCoroutine {
        cont ->
            getMediaManager()
                .upload(imageUri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        println("Cloudinary upload started")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        val progress = (bytes.toDouble() / totalBytes.toDouble()) * 100
                        println("Cloudinary upload progress: $progress%")
                    }

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        println("Cloudinary upload success: $resultData")
                        val imageUrl = resultData?.get("secure_url") as? String
                        if (imageUrl != null) {
                            println("Cloudinary uploaded image URL: $imageUrl")
                            cont.resume(imageUrl)
                        } else {
                            cont.resumeWithException(Exception("Upload succeeded but URL is null"))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        println("Cloudinary upload error: ${error?.description}")
                        cont.resumeWithException(Exception("Upload failed: ${error?.description}"))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        println("Cloudinary upload rescheduled: ${error?.description}")
                        cont.resumeWithException(Exception("Upload rescheduled: ${error?.description}"))
                    }
                })
                .dispatch()
    }
}