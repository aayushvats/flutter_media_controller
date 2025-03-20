import 'package:flutter/material.dart';
import 'package:flutter_media_controller/flutter_media_controller.dart';

void main() {
  runApp(const MediaInfoApp());
}

class MediaInfoApp extends StatefulWidget {
  const MediaInfoApp({super.key});

  @override
  State<MediaInfoApp> createState() => _MediaInfoAppState();
}

class _MediaInfoAppState extends State<MediaInfoApp> {
  String title = "Unknown";
  String artist = "Unknown";

  @override
  void initState() {
    super.initState();
    fetchMediaInfo();
  }

  Future<void> fetchMediaInfo() async {
    await FlutterMediaController.requestPermissions();
    final mediaInfo = await FlutterMediaController.getCurrentMediaInfo();
      setState(() {
        title = mediaInfo.track;
        artist = mediaInfo.artist;
      });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Current Media Info')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text("Title: $title", style: const TextStyle(fontSize: 20)),
              Text("Artist: $artist", style: const TextStyle(fontSize: 18)),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: fetchMediaInfo,
                child: const Text("Refresh"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
