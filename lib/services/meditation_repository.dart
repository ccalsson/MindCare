import 'package:firebase_auth/firebase_auth.dart';
import './firebase_service.dart';
import '../models/meditation_models.dart';

class MeditationRepository {
  final FirebaseService _firebaseService = FirebaseService();
  final FirebaseAuth _auth = FirebaseAuth.instance;
  final String baseUrl;
  
  MeditationRepository({required this.baseUrl});
  
  Future<List<MeditationSession>> getAllSessions() async {
    return await _firebaseService.getAllSessions();
  }
  
  Future<List<MeditationSession>> getSessionsByCategory(MeditationCategory category) async {
    return await _firebaseService.getSessionsByCategory(category);
  }
  
  Future<void> saveMeditationProgress(String sessionId, int duration) async {
    final user = _auth.currentUser;
    if (user != null) {
      await _firebaseService.saveMeditationHistory(
        userId: user.uid,
        sessionId: sessionId,
        duration: duration,
        completedAt: DateTime.now(),
      );
    }
  }
  
  // Removed unused dev-only helpers to satisfy analyzer.
} 
