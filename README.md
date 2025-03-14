# flutter_media_controller

Helps control media from Android Devices

## Getting Started

Add the following to your project to use this

~~~dart
<service
android:name="com.example.flutter_media_controller.MediaNotificationListener"
android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
android:exported="false">
<intent-filter>
<action android:name="android.service.notification.NotificationListenerService" />
</intent-filter>
</service>
~~~
