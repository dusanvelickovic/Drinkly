package com.example.drinkly.data.helper

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "notification_channel_id"

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createNotificationChannel()
    }

    // Create a Notification Channel (Required for Android O / API 26+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General Notifications"
            val descriptionText = "Notifications for general app updates."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Build and Dispatch the Notification
    fun showNotification(title: String, message: String, notificationId: Int) {
        // Handle POST_NOTIFICATIONS permission for API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // In a real app, you would request the permission here
                return
            }
        }

        // Optional: Create an Intent to open the main activity when the user taps the notification
        val intent = Intent(context, context.javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE or FLAG_MUTABLE
        )

        // Build the Notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.example.drinkly.R.drawable.glass_icon) // Use your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true) // Dismiss the notification when the user taps it

        // Dispatch the Notification
        notificationManager.notify(notificationId, builder.build())
    }
}