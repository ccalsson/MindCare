import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_analytics/firebase_analytics.dart';
import '../models/screening_form.dart';
import '../models/screening_result.dart';

class ScreeningRepo {
  final FirebaseFirestore _db;
  final FirebaseAnalytics _analytics;
  ScreeningRepo({FirebaseFirestore? db, FirebaseAnalytics? analytics})
      : _db = db ?? FirebaseFirestore.instance,
        _analytics = analytics ?? FirebaseAnalytics.instance;

  Future<ScreeningResult> save(
    String uid,
    String instrument,
    int version,
    List<Answer> answers,
  ) async {
    final score = computeScore(instrument, answers);
    final band = bandFromScore(instrument, score);
    final result = ScreeningResult(
      instrument: instrument,
      version: version,
      score: score,
      riskBand: band,
      createdAt: DateTime.now(),
    );

    final userRef = _db.collection('users').doc(uid);
    final screenings = userRef.collection('screenings');
    final batch = _db.batch();
    final docRef = screenings.doc();
    batch.set(docRef, result.toMap());
    batch.set(userRef, {'riskLevel': band}, SetOptions(merge: true));
    await batch.commit();

    await _analytics.logEvent(name: 'screening_completed', parameters: {
      'instrument': instrument,
      'version': version,
      'score': score,
      'risk_band': band,
    });

    return result;
  }

  int computeScore(String instrument, List<Answer> answers) {
    switch (instrument.toUpperCase()) {
      case 'AUDIT':
        // Sumatoria simple asumiendo valores 0..4
        int sum = 0;
        for (final a in answers) {
          final v = a.value;
          if (v is num) sum += v.toInt();
        }
        return sum;
      default:
        // TODO(ccalsson): instrumentos adicionales (DAST, ASSIST, IGDS9)
        int sum = 0;
        for (final a in answers) {
          final v = a.value;
          if (v is num) sum += v.toInt();
        }
        return sum;
    }
  }

  String bandFromScore(String instrument, int score) {
    switch (instrument.toUpperCase()) {
      case 'AUDIT':
        if (score <= 7) return 'low';
        if (score <= 15) return 'moderate';
        return 'high';
      default:
        // TODO(ccalsson): bandas por instrumento
        if (score <= 7) return 'low';
        if (score <= 15) return 'moderate';
        return 'high';
    }
  }
}

