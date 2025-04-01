import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import '../models/meditation_models.dart';

class FirebaseService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final FirebaseStorage _storage = FirebaseStorage.instance;

  // Obtener todas las sesiones de meditación
  Future<List<MeditationSession>> getAllSessions() async {
    try {
      final snapshot = await _firestore.collection('meditation_sessions').get();
      return snapshot.docs.map((doc) {
        final data = doc.data();
        return MeditationSession.fromMap({
          'id': doc.id,
          ...data,
        });
      }).toList();
    } catch (e) {
      print('Error getting sessions: $e');
      throw e;
    }
  }

  // Obtener sesiones por categoría
  Future<List<MeditationSession>> getSessionsByCategory(MeditationCategory category) async {
    try {
      final snapshot = await _firestore
          .collection('meditation_sessions')
          .where('category', isEqualTo: category.toString())
          .get();
      
      return snapshot.docs.map((doc) {
        final data = doc.data();
        return MeditationSession.fromMap({
          'id': doc.id,
          ...data,
        });
      }).toList();
    } catch (e) {
      print('Error getting sessions by category: $e');
      throw e;
    }
  }

  // Obtener URL de audio
  Future<String> getAudioUrl(String audioPath) async {
    try {
      final ref = _storage.ref().child('audio/$audioPath');
      return await ref.getDownloadURL();
    } catch (e) {
      print('Error getting audio URL: $e');
      throw e;
    }
  }

  // Guardar historial de meditación
  Future<void> saveMeditationHistory({
    required String userId,
    required String sessionId,
    required int duration,
    required DateTime completedAt,
  }) async {
    try {
      await _firestore
          .collection('users')
          .doc(userId)
          .collection('meditation_history')
          .add({
        'sessionId': sessionId,
        'duration': duration,
        'completedAt': completedAt.toIso8601String(),
      });
    } catch (e) {
      print('Error saving meditation history: $e');
      throw e;
    }
  }
} 