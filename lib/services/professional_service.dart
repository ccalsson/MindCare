import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/professional.dart';

class ProfessionalService {
  final _db = FirebaseFirestore.instance;

  Future<List<Professional>> list({String? specialty, String? language}) async {
    Query col = _db.collection('professionals').where('isActive', isEqualTo: true);
    if (specialty != null && specialty.isNotEmpty) {
      col = col.where('specialties', arrayContains: specialty);
    }
    if (language != null && language.isNotEmpty) {
      col = col.where('languages', arrayContains: language);
    }
    final snap = await col.limit(50).get();
    return snap.docs.map((d) => Professional.fromMap(d.data() as Map<String, dynamic>, d.id)).toList();
  }

  Future<Professional?> getById(String proId) async {
    final doc = await _db.collection('professionals').doc(proId).get();
    if (!doc.exists) return null;
    return Professional.fromMap(doc.data() as Map<String, dynamic>, doc.id);
  }
}

