package com.example.flutter_media_controller

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.net.Uri
import android.util.Base64
import android.widget.RemoteViews
import android.media.session.PlaybackState

class MediaControlWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // Fetch track info from shared preferences
        val sharedPreferences = context.getSharedPreferences("media_widget_prefs", Context.MODE_PRIVATE)

        val track = sharedPreferences.getString("track", "No track playing") ?: "No track playing"
        val artist = sharedPreferences.getString("artist", "Unknown artist") ?: "Unknown artist"
        val thumbnailBase64 = sharedPreferences.getString("thumbnail", null)
        val isPlaying = sharedPreferences.getBoolean("isPlaying", false)

        views.setTextViewText(R.id.track_title, track)
        views.setTextViewText(R.id.artist_name, artist)

        // Update play/pause button image
        views.setImageViewResource(
                R.id.button_play_pause,
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )

        // Decode and set the thumbnail image
        if (!thumbnailBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(thumbnailBase64.replace("\\s".toRegex(), ""), Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                views.setImageViewBitmap(R.id.thumbnail, bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Set control button actions
        setControlButtons(context, views, appWidgetId)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setControlButtons(context: Context, views: RemoteViews, appWidgetId: Int) {
        views.setOnClickPendingIntent(
                R.id.button_previous,
                getPendingSelfIntent(context, "previous", appWidgetId)
        )
        views.setOnClickPendingIntent(
                R.id.button_play_pause,
                getPendingSelfIntent(context, "playPause", appWidgetId)
        )
        views.setOnClickPendingIntent(
                R.id.button_next,
                getPendingSelfIntent(context, "next", appWidgetId)
        )
    }

    private fun getPendingSelfIntent(
            context: Context,
            action: String,
            appWidgetId: Int
    ): PendingIntent {
        val intent = Intent(context, MediaControlWidgetProvider::class.java).apply {
            this.action = action
            this.data = Uri.parse("homewidget://media_action?action=$action&widgetId=$appWidgetId")
        }

        return PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            "previous" -> sendMediaCommand(context, "previous")
            "playPause" -> sendMediaCommand(context, "playPause")
            "next" -> sendMediaCommand(context, "next")
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                        ComponentName(context, MediaControlWidgetProvider::class.java)
                )
                for (appWidgetId in appWidgetIds) {
                    updateWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    private fun sendMediaCommand(context: Context, action: String) {
        val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

        try {
            val controllers = mediaSessionManager.getActiveSessions(null)
            if (controllers.isNotEmpty()) {
                val mediaController = controllers[0]
                when (action) {
                    "previous" -> mediaController.transportControls.skipToPrevious()
                    "playPause" -> {
                        if (mediaController.playbackState?.state == PlaybackState.STATE_PLAYING) {
                            mediaController.transportControls.pause()
                        } else {
                            mediaController.transportControls.play()
                        }
                    }
                    "next" -> mediaController.transportControls.skipToNext()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
