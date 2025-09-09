import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/availability.dart';

class AvailabilityService {
  final _db = FirebaseFirestore.instance;

  Future<List<AvailabilitySlot>> getWeekly(String proId) async {
    final doc = await _db.collection('availability').doc(proId).get();
    final data = (doc.data() ?? const {}) as Map<String, dynamic>;
    final slots = (data['slots'] as List<dynamic>? ?? const [])
        .map((s) => AvailabilitySlot(
              dayOfWeek: (s['dayOfWeek'] as num?)?.toInt() ?? 1,
              startTime: s['start'] as String? ?? '10:00',
              endTime: s['end'] as String? ?? '12:00',
              exceptions: const [],
            ))
        .toList();
    return slots;
  }

  Future<List<DateTime>> getExceptions(String proId) async {
    final doc = await _db.collection('availability').doc(proId).get();
    final data = (doc.data() ?? const {}) as Map<String, dynamic>;
    final exc = (data['exceptions'] as List<dynamic>? ?? const [])
        .map((e) => (e is Timestamp) ? e.toDate() : DateTime.tryParse('$e'))
        .whereType<DateTime>()
        .map((d) => DateTime(d.year, d.month, d.day))
        .toList();
    return exc;
  }

  Future<void> setWeekly(String proId, List<AvailabilitySlot> slots) async {
    await _db.collection('availability').doc(proId).set({
      'slots': slots
          .map((s) => {
                'dayOfWeek': s.dayOfWeek,
                'start': s.startTime,
                'end': s.endTime,
              })
          .toList(),
      'updatedAt': FieldValue.serverTimestamp(),
    }, SetOptions(merge: true));
  }

  Future<void> addException(String proId, DateTime day) async {
    await _db.collection('availability').doc(proId).set({
      'exceptions': FieldValue.arrayUnion([Timestamp.fromDate(DateTime(day.year, day.month, day.day))])
    }, SetOptions(merge: true));
  }
}
