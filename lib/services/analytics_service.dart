import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:cache/cache.dart';

class AnalyticsService {
  final FirebaseAnalytics _analytics = FirebaseAnalytics.instance;
  final _firestore = FirebaseFirestore.instance;

  Future<void> logUserActivity({
    required String userId,
    required String activityType,
    required Map<String, dynamic> data,
  }) async {
    // Registrar en Analytics
    await _analytics.logEvent(
      name: activityType,
      parameters: {
        'user_id': userId,
        ...data,
      },
    );

    // Guardar en Firestore para análisis detallado
    await _firestore
        .collection('users')
        .doc(userId)
        .collection('activity_logs')
        .add({
      'type': activityType,
      'timestamp': FieldValue.serverTimestamp(),
      'data': data,
    });
  }

  Future<Map<String, dynamic>> getUserInsights(String userId) async {
    try {
      final now = DateTime.now();
      final startOfWeek = now.subtract(
        Duration(days: now.weekday - 1),
      );

      final logs = await _firestore
          .collection('users')
          .doc(userId)
          .collection('activity_logs')
          .where('timestamp', isGreaterThan: startOfWeek)
          .get();

      return {
        'weeklyActivity': _analyzeWeeklyActivity(logs.docs),
        'preferredTimes': _analyzePreferredTimes(logs.docs),
        'completionRate': _calculateCompletionRate(logs.docs),
        'streaks': _calculateStreaks(logs.docs),
        'improvements': _analyzeImprovements(logs.docs),
      };
    } catch (e) {
      print('Error getting user insights: $e');
      return {};
    }
  }

  Map<String, int> _analyzeWeeklyActivity(List<QueryDocumentSnapshot> docs) {
    final activity = <String, int>{};
    for (var doc in docs) {
      final type = doc.data()['type'] as String;
      activity[type] = (activity[type] ?? 0) + 1;
    }
    return activity;
  }

  Map<String, int> _analyzePreferredTimes(List<QueryDocumentSnapshot> docs) {
    final times = <String, int>{};
    for (var doc in docs) {
      final timestamp = (doc.data()['timestamp'] as Timestamp).toDate();
      final hour = timestamp.hour;
      times[hour.toString()] = (times[hour.toString()] ?? 0) + 1;
    }
    return times;
  }

  double _calculateCompletionRate(List<QueryDocumentSnapshot> docs) {
    final started = docs.where((doc) => 
      doc.data()['type'] == 'session_started').length;
    final completed = docs.where((doc) => 
      doc.data()['type'] == 'session_completed').length;
    
    return started > 0 ? completed / started : 0;
  }

  int _calculateStreaks(List<QueryDocumentSnapshot> docs) {
    final dates = docs
        .map((doc) => (doc.data()['timestamp'] as Timestamp).toDate())
        .map((date) => DateTime(date.year, date.month, date.day))
        .toSet()
        .toList()
      ..sort();

    int currentStreak = 0;
    int maxStreak = 0;
    DateTime? lastDate;

    for (var date in dates) {
      if (lastDate == null || 
          date.difference(lastDate).inDays == 1) {
        currentStreak++;
      } else {
        currentStreak = 1;
      }
      maxStreak = currentStreak > maxStreak ? currentStreak : maxStreak;
      lastDate = date;
    }

    return maxStreak;
  }

  Map<String, dynamic> _analyzeImprovements(List<QueryDocumentSnapshot> docs) {
    // Implementar lógica de análisis de mejoras
    return {};
  }
}

class CachedAnalyticsService {
  final Cache _cache;
  
  Future<Map<String, dynamic>> getUserInsights(String userId) async {
    // Verificar caché primero
    final cached = await _cache.get('user_insights_$userId');
    if (cached != null && !_isExpired(cached)) {
      return cached;
    }

    // Realizar batch read
    final batch = _firestore.batch();
    final insights = await Future.wait([
      _getWeeklyActivity(userId, batch),
      _getPreferredTimes(userId, batch),
      _getCompletionRate(userId, batch),
    ]);

    await _cache.set('user_insights_$userId', insights);
    return insights;
  }
} 