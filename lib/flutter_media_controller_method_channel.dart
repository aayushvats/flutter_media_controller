import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_media_controller_platform_interface.dart';

/// An implementation of [FlutterMediaControllerPlatform] that uses method channels.
class MethodChannelFlutterMediaController
    extends FlutterMediaControllerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_media_controller');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
