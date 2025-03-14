import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_media_controller/flutter_media_controller.dart';
import 'package:flutter_media_controller/flutter_media_controller_platform_interface.dart';
import 'package:flutter_media_controller/flutter_media_controller_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterMediaControllerPlatform
    with MockPlatformInterfaceMixin
    implements FlutterMediaControllerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterMediaControllerPlatform initialPlatform = FlutterMediaControllerPlatform.instance;

  test('$MethodChannelFlutterMediaController is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterMediaController>());
  });

  test('getPlatformVersion', () async {
    FlutterMediaController flutterMediaControllerPlugin = FlutterMediaController();
    MockFlutterMediaControllerPlatform fakePlatform = MockFlutterMediaControllerPlatform();
    FlutterMediaControllerPlatform.instance = fakePlatform;

    expect(await flutterMediaControllerPlugin.getPlatformVersion(), '42');
  });
}
