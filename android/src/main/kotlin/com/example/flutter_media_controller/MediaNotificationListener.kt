package com.example.flutter_media_controller

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.media.session.MediaSession
import android.media.session.MediaController
import android.util.Log

class MediaNotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.notification?.extras != null) {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") ?: "Unknown"
            val artist = extras.getString("android.text") ?: "Unknown"

            Log.d("MediaNotification", "Title: $title, Artist: $artist")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d("MediaNotification", "Notification Removed: ${sbn.packageName}")
    }
}
