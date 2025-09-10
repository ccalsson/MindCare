import 'package:firebase_remote_config/firebase_remote_config.dart';

class AddictionsRemoteConfigService {
  final FirebaseRemoteConfig _rc;
  AddictionsRemoteConfigService({FirebaseRemoteConfig? rc})
      : _rc = rc ?? FirebaseRemoteConfig.instance;

  Future<void> fetchAndActivate() async {
    await _rc.fetchAndActivate();
  }

  bool get enableCommunity => _rc.getBool('enableCommunity');
  List<String> get instrumentsEnabled {
    final raw = _rc.getString('instrumentsEnabled');
    if (raw.isEmpty) return const [];
    return raw
        .replaceAll('[', '')
        .replaceAll(']', '')
        .split(',')
        .map((e) => e.trim().replaceAll('"', ''))
        .where((e) => e.isNotEmpty)
        .toList();
  }

  String get crisisHotlinesJson => _rc.getString('crisisHotlines');
  int get minAppVersionForAddictions => _rc.getInt('minAppVersionForAddictions');
}
