# flutter_media_controller

A Flutter plugin that leverages Android's built-in APIs to detect and control media playback from your Flutter application.

## Features

- Detect currently playing media information (title, artist, playing status, thumbnail)
- Control media playback (play/pause, next track, previous track)
- Works with most media apps that show media notifications

## Installation

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  flutter_media_controller: ^1.0.0
```

Then run:

```bash
flutter pub get
```

## Setup

### Android

#### 1. Add Service to AndroidManifest.xml

Add the following service declaration inside the `<application>` tag in your `android/app/src/main/AndroidManifest.xml` file:

```xml
<service
    android:name="com.example.flutter_media_controller.MediaNotificationListener"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

#### 2. Add Permissions

Add the following permission to your `android/app/src/main/AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Usage

### Request Permissions

The plugin requires notification listener permission to work. You need to request this permission before using other functions:

```dart
// Request notification listener permission
await FlutterMediaController.requestPermissions();
```

This will open the system settings page where the user needs to enable notification access for your app.

### Get Current Media Information

To get information about the currently playing media:

```dart
Future<void> fetchMediaInfo() async {
  final mediaInfo = await FlutterMediaController.getCurrentMediaInfo();
  setState(() {
    title = mediaInfo.track;
    artist = mediaInfo.artist;
    isPlaying = mediaInfo.isPlaying;
    thumbnail = mediaInfo.thumbnail; // ByteArray that can be used with Image.memory
  });
}
```

### Control Media Playback

#### Toggle Play/Pause

```dart
await FlutterMediaController.togglePlayPause();
```

#### Skip to Next Track

```dart
await FlutterMediaController.nextTrack();
```

#### Go to Previous Track

```dart
await FlutterMediaController.previousTrack();
```

## Example

```dart
import 'package:flutter/material.dart';
import 'package:flutter_media_controller/flutter_media_controller.dart';

class MediaControlExample extends StatefulWidget {
  @override
  _MediaControlExampleState createState() => _MediaControlExampleState();
}

class _MediaControlExampleState extends State<MediaControlExample> {
  String title = 'Unknown';
  String artist = 'Unknown';
  bool isPlaying = false;
  Uint8List? thumbnail;

  @override
  void initState() {
    super.initState();
    requestPermissionAndFetchData();
  }

  Future<void> requestPermissionAndFetchData() async {
    await FlutterMediaController.requestPermissions();
    fetchMediaInfo();
  }

  Future<void> fetchMediaInfo() async {
    final mediaInfo = await FlutterMediaController.getCurrentMediaInfo();
    setState(() {
      title = mediaInfo.track;
      artist = mediaInfo.artist;
      isPlaying = mediaInfo.isPlaying;
      thumbnail = mediaInfo.thumbnail;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Media Controller')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (thumbnail != null)
              Image.memory(
                thumbnail!,
                width: 200,
                height: 200,
                fit: BoxFit.cover,
              ),
            SizedBox(height: 20),
            Text('Now Playing: $title', style: TextStyle(fontSize: 18)),
            Text('Artist: $artist', style: TextStyle(fontSize: 16)),
            SizedBox(height: 30),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  icon: Icon(Icons.skip_previous, size: 36),
                  onPressed: () async {
                    await FlutterMediaController.previousTrack();
                    fetchMediaInfo();
                  },
                ),
                IconButton(
                  icon: Icon(
                    isPlaying ? Icons.pause : Icons.play_arrow,
                    size: 36,
                  ),
                  onPressed: () async {
                    await FlutterMediaController.togglePlayPause();
                    fetchMediaInfo();
                  },
                ),
                IconButton(
                  icon: Icon(Icons.skip_next, size: 36),
                  onPressed: () async {
                    await FlutterMediaController.nextTrack();
                    fetchMediaInfo();
                  },
                ),
              ],
            ),
            ElevatedButton(
              onPressed: fetchMediaInfo,
              child: Text('Refresh Media Info'),
            ),
          ],
        ),
      ),
    );
  }
}
```

## Limitations

- This plugin currently only supports Android
- Not all media apps expose their information through the notification system

## License

This project is licensed under the MIT License - see the LICENSE file for details.