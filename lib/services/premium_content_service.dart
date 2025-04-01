import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/resource_model.dart';

class PremiumContentService {
  final _firestore = FirebaseFirestore.instance;

  Future<List<AudioResource>> getPremiumContent(String userId) async {
    try {
      // Verificar suscripción premium
      final userDoc = await _firestore.collection('users').doc(userId).get();
      final subscription = userDoc.data()?['subscription'];
      
      enum SubscriptionType { free, basic, plus, premium }
      if (subscription?.type != SubscriptionType.premium) {
        throw UnauthorizedAccessException('Requiere suscripción premium');
      }

      // Obtener contenido premium
      final snapshot = await _firestore
          .collection('premium_content')
          .where('isActive', isEqualTo: true)
          .orderBy('releaseDate', descending: true)
          .get();

      return snapshot.docs
          .map((doc) => AudioResource.fromMap(doc.data()))
          .toList();
    } catch (e) {
      print('Error getting premium content: $e');
      return [];
    }
  }

  Stream<List<AudioResource>> streamNewPremiumContent() {
    return _firestore
        .collection('premium_content')
        .where('isActive', isEqualTo: true)
        .orderBy('releaseDate', descending: true)
        .limit(10)
        .snapshots()
        .map((snapshot) => snapshot.docs
            .map((doc) => AudioResource.fromMap(doc.data()))
            .toList());
  }
} 