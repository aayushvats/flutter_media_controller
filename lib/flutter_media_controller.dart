import 'package:flutter/services.dart';

class MediaInfo {
  final String track;
  final String artist;
  final String thumbnailUrl;
  final bool isPlaying;

  MediaInfo({
    required this.track,
    required this.artist,
    required this.thumbnailUrl,
    required this.isPlaying,
  });
}

class FlutterMediaController {
  static const MethodChannel _channel = MethodChannel('flutter_media_controller');

  static Future<MediaInfo> getCurrentMediaInfo() async {
    try {
      final result = await _channel.invokeMethod<Map>('getMediaInfo');
      return MediaInfo(
        track: result?['track'] ?? 'No track playing',
        artist: result?['artist'] ?? 'Unknown artist',
        thumbnailUrl: result?['thumbnailUrl'] ?? '',
        isPlaying: result?['isPlaying'] ?? false,
      );
    } on PlatformException catch (e) {
      return MediaInfo(
        track: 'Error ${e} getting track info',
        artist: 'Unknown artist',
        thumbnailUrl: '',
        isPlaying: false,
      );
    }
  }

  static Future<void> handleAction(String action) async {
    // try {
      await _channel.invokeMethod('mediaAction', {'action': action});
    // } on PlatformException catch (e) {
    //   print("Failed to perform media action: ${e.message}");
    // }
  }

  static Future<void> togglePlayPause() async {
    await handleAction('playPause');
  }

  static Future<void> nextTrack() async {
    await handleAction('next');
  }

  static Future<void> previousTrack() async {
    await handleAction('previous');
  }
}
