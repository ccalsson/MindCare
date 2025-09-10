import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/plan.dart';

class PlansRepo {
  final FirebaseFirestore _db;
  PlansRepo({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  Future<void> createOrUpdate(String uid, Plan plan) async {
    final ref = _db.collection('users').doc(uid).collection('plans').doc('current');
    await ref.set(plan.toMap());
  }

  Future<Plan?> getCurrent(String uid) async {
    final ref = _db.collection('users').doc(uid).collection('plans').doc('current');
    final snap = await ref.get();
    if (!snap.exists) return null;
    return Plan.fromMap(snap.data() as Map<String, dynamic>);
  }
}

