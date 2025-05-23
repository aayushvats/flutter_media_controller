package com.example.flutter_media_controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.util.Base64
import android.graphics.Bitmap
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager
//import android.content.Intent
//import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.app.Activity
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class FlutterMediaControllerPlugin: FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {

  private var activity: Activity? = null
  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private val PERMISSION_REQUEST_CODE = 1001

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_media_controller")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "requestPermissions" -> {
        try {
          requestPermissions()
          result.success(true)
        } catch (e: Exception) {
          result.error("MEDIA_ERROR", "Failed to get media info", e.message)
        }
      }
      "getMediaInfo" -> {
        try {
          val mediaInfo = getCurrentMediaInfo()
          result.success(mediaInfo)
        } catch (e: Exception) {
          result.error("MEDIA_ERROR", "Failed to get media info", e.message)
        }
      }
      "mediaAction" -> {
        val action = call.argument<String>("action")
        if (action != null) {
          handleMediaAction(action)
          result.success(null)
        } else {
          result.error("INVALID_ARGUMENT", "Action cannot be null", null)
        }
      }
      else -> result.notImplemented()
    }
  }

  private fun getCurrentMediaInfo(): Map<String, Any> {
    val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    val componentName = ComponentName(context, MediaControlWidgetProvider::class.java)

    try {
      val controllers = mediaSessionManager.getActiveSessions(componentName)
      if (controllers.isNotEmpty()) {
        val controller = controllers[0]
        val metadata = controller.metadata
        val playbackState = controller.playbackState

        var thumbnailBase64 = ""
        metadata?.let {
          val artwork = it.getBitmap(MediaMetadata.METADATA_KEY_ART) ?: it.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
          if (artwork != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            artwork.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            thumbnailBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
          }
        }

        return mapOf(
                "track" to (metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Track"),
                "artist" to (metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"),
                "thumbnailUrl" to thumbnailBase64,
                "isPlaying" to (playbackState?.state == PlaybackState.STATE_PLAYING)
        )
      }
    } catch (e: SecurityException) {
      e.printStackTrace()
    }

    return mapOf(
            "track" to "No track playing",
            "artist" to "Unknown artist",
            "thumbnailUrl" to "",
            "isPlaying" to false
    )
  }

  private fun handleMediaAction(action: String) {
    val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    val componentName = ComponentName(context, MediaControlWidgetProvider::class.java)

    try {
      val controllers = mediaSessionManager.getActiveSessions(componentName)
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

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
//    requestPermissions()
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  fun requestPermissions() {
    activity?.let { act ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(act, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(
                  act,
                  arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                  PERMISSION_REQUEST_CODE
          )
        }
      }
      act.startActivity(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    } ?: run {
      // Log or handle the case when activity is null
      println("Activity is null, cannot request permissions")
    }
  }

  companion object {
    private const val PERMISSION_REQUEST_CODE = 1001
  }

}
