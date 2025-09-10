import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/share_preference.dart';

class SharePrefsService {
  final FirebaseFirestore _db;
  SharePrefsService({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  DocumentReference<Map<String, dynamic>> _doc(String userId, String id) =>
      _db.collection('teacher_wellbeing').doc(userId).collection('shares').doc(id);

  Future<void> save(SharePreference p) async {
    await _doc(p.userId, p.id).set(p.toJson());
  }

  Future<SharePreference?> get(String userId, String id) async {
    final snap = await _doc(userId, id).get();
    if (!snap.exists) return null;
    return SharePreference.fromJson(snap.data()!, snap.id);
  }
}

