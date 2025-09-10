import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/wellbeing_resource.dart';

class ResourcesService {
  final FirebaseFirestore _db;
  ResourcesService({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  Stream<List<WellbeingResource>> watchResources({List<String>? topics, String? kind, int? maxDuration}) {
    Query<Map<String, dynamic>> q = _db.collection('resources');
    if (kind != null) q = q.where('kind', isEqualTo: kind);
    if (maxDuration != null) q = q.where('durationMin', isLessThanOrEqualTo: maxDuration);
    // Firestore doesn't support array-contains-any + other inequality combos reliably in all indexes; keep simple.
    return q.snapshots().map((s) => s.docs
        .map((d) => WellbeingResource.fromJson(d.data(), d.id))
        .where((r) => topics == null || topics.isEmpty || r.topics.any((t) => topics.contains(t)))
        .toList());
  }
}

