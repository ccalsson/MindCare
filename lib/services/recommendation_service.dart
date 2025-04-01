import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/resource_model.dart';
import '../exceptions/recommendation_exception.dart';
import '../services/analytics_service.dart';

abstract class IDataRepository {
  Future<List<ActivityLog>> getUserHistory(String userId);
}

class RecommendationService {
  final IDataRepository _repository;
  final IAnalyticsService _analytics;
  
  RecommendationService(this._repository, this._analytics);

  final _firestore = FirebaseFirestore.instance;

  Future<List<AudioResource>> getPersonalizedRecommendations(String userId) async {
    try {
      // Obtener historial del usuario
      final userHistory = await _getUserHistory(userId);
      
      // Obtener preferencias del usuario
      final userPreferences = await _getUserPreferences(userId);
      
      // Obtener recursos basados en el análisis
      final recommendations = await _analyzeAndRecommend(
        userHistory,
        userPreferences,
      );
      
      return recommendations;
    } catch (e, stackTrace) {
      await _analytics.logError(
        error: e,
        stackTrace: stackTrace,
        reason: 'Error getting recommendations',
      );
      throw RecommendationException('No se pudieron obtener recomendaciones', e);
    }
  }

  Future<Map<String, dynamic>> _getUserHistory(String userId) async {
    final history = await _firestore
        .collection('users')
        .doc(userId)
        .collection('activity_history')
        .orderBy('timestamp', descending: true)
        .limit(50)
        .get();

    return {
      'resources': history.docs.map((doc) => doc.data()).toList(),
      'categories': _analyzePreferredCategories(history.docs),
      'timeOfDay': _analyzePreferredTimes(history.docs),
    };
  }

  Map<String, int> _analyzePreferredCategories(List<QueryDocumentSnapshot> docs) {
    final categories = <String, int>{};
    for (var doc in docs) {
      final category = doc.data()['category'] as String;
      categories[category] = (categories[category] ?? 0) + 1;
    }
    return categories;
  }

  Map<String, int> _analyzePreferredTimes(List<QueryDocumentSnapshot> docs) {
    final times = <String, int>{};
    for (var doc in docs) {
      final timestamp = DateTime.parse(doc['timestamp'] as String);
      final hour = timestamp.hour;
      final timeOfDay = _getTimeOfDay(hour);
      times[timeOfDay] = (times[timeOfDay] ?? 0) + 1;
    }
    return times;
  }

  String _getTimeOfDay(int hour) {
    if (hour >= 5 && hour < 12) return 'morning';
    if (hour >= 12 && hour < 17) return 'afternoon';
    if (hour >= 17 && hour < 22) return 'evening';
    return 'night';
  }
} 