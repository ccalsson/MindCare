import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_analytics/firebase_analytics.dart';
import '../models/journal_entry.dart';

class JournalRepo {
  final FirebaseFirestore _db;
  final FirebaseAnalytics _analytics;
  JournalRepo({FirebaseFirestore? db, FirebaseAnalytics? analytics})
      : _db = db ?? FirebaseFirestore.instance,
        _analytics = analytics ?? FirebaseAnalytics.instance;

  Future<void> addEntry(String uid, JournalEntry e) async {
    final ref = _db.collection('users').doc(uid).collection('journals').doc();
    await ref.set(e.toMap());

    await _analytics.logEvent(name: 'checkin_submitted', parameters: {
      'mood': e.mood,
      'craving': e.craving,
    });

    if (e.craving >= 8) {
      await _analytics.logEvent(name: 'craving_high');
    }
  }
}

