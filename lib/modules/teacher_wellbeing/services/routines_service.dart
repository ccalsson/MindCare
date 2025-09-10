import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/routine.dart';

class RoutinesService {
  final FirebaseFirestore _db;
  RoutinesService({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  Stream<List<Routine>> watchRoutines({int? maxDuration}) {
    Query<Map<String, dynamic>> q = _db.collection('routines');
    if (maxDuration != null) q = q.where('durationMin', isLessThanOrEqualTo: maxDuration);
    return q.snapshots().map((s) => s.docs.map((d) => Routine.fromJson(d.data(), d.id)).toList());
  }
}

