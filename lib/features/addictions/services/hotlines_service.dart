import 'dart:convert';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_remote_config/firebase_remote_config.dart';
import '../models/hotline.dart';

class HotlinesService {
  final FirebaseRemoteConfig _rc;
  final FirebaseFirestore _db;
  HotlinesService({FirebaseRemoteConfig? rc, FirebaseFirestore? db})
      : _rc = rc ?? FirebaseRemoteConfig.instance,
        _db = db ?? FirebaseFirestore.instance;

  Future<List<Hotline>> getHotlines({required String countryCode}) async {
    // Try Remote Config JSON first
    try {
      final raw = _rc.getString('crisisHotlines');
      if (raw.isNotEmpty) {
        final map = json.decode(raw) as Map<String, dynamic>;
        final list = (map[countryCode.toUpperCase()] as List?) ?? const [];
        return list
            .map((e) => Hotline.fromMap(Map<String, dynamic>.from(e)))
            .toList();
      }
    } catch (_) {}

    // Fallback to Firestore /flags/global
    try {
      final snap = await _db.collection('flags').doc('global').get();
      if (snap.exists) {
        final data = snap.data() as Map<String, dynamic>;
        final map = Map<String, dynamic>.from(data['crisisHotlines'] ?? {});
        final list = (map[countryCode.toUpperCase()] as List?) ?? const [];
        return list
            .map((e) => Hotline.fromMap(Map<String, dynamic>.from(e)))
            .toList();
      }
    } catch (_) {}

    return const [];
  }
}

