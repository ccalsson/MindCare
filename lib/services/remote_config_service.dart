import 'package:firebase_remote_config/firebase_remote_config.dart';
import '../config/video_config.dart';

class RemoteConfigService {
  final _rc = FirebaseRemoteConfig.instance;

  Future<void> init() async {
    await _rc.setDefaults({VideoConfig.featureFlagKey: true});
    await _rc.fetchAndActivate();
  }

  bool get enableProCalls => _rc.getBool(VideoConfig.featureFlagKey);
}

