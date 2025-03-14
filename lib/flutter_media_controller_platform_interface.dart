import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_media_controller_method_channel.dart';

abstract class FlutterMediaControllerPlatform extends PlatformInterface {
  /// Constructs a FlutterMediaControllerPlatform.
  FlutterMediaControllerPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterMediaControllerPlatform _instance = MethodChannelFlutterMediaController();

  /// The default instance of [FlutterMediaControllerPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterMediaController].
  static FlutterMediaControllerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterMediaControllerPlatform] when
  /// they register themselves.
  static set instance(FlutterMediaControllerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
