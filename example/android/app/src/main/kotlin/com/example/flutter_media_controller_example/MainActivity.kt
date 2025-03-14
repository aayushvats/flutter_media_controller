package com.example.flutter_media_controller_example

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.media.session.MediaSessionManager
import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.media.MediaMetadata
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

import android.os.Build
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity: FlutterActivity() {

    private val CHANNEL = "mediaController"
    private val PERMISSION_REQUEST_CODE = 123
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        requestPermissions()
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "getMediaInfo" -> {
                    try {
                        result.success("")
                    } catch (e: Exception) {
                        result.error("MEDIA_ERROR", "Failed to get media info", e.message)
                    }
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        PERMISSION_REQUEST_CODE
                )
            }
        }

        // Open notification listener settings
        startActivity(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}
