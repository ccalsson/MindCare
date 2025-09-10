import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/teacher_checkin.dart';
import '../models/teacher_summary.dart';

class CheckinService {
  final FirebaseFirestore _db;
  CheckinService({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  CollectionReference<Map<String, dynamic>> _userRoot(String userId) =>
      _db.collection('teacher_wellbeing').doc(userId).collection('checkins');

  Future<void> createCheckin(TeacherCheckin c) async {
    // Anti-spam: one check-in every 2 hours
    final recent = await _userRoot(c.userId)
        .orderBy('createdAt', descending: true)
        .limit(1)
        .get();
    if (recent.docs.isNotEmpty) {
      final last = DateTime.tryParse(recent.docs.first.data()['createdAt']?.toString() ?? '');
      if (last != null) {
        final diff = c.createdAt.difference(last);
        if (diff.inMinutes < 120) {
          throw StateError('Check-in permitido cada 2 horas');
        }
      }
    }
    await _userRoot(c.userId).add(c.toJson());
  }

  Stream<List<TeacherCheckin>> watchCheckins(String userId, {DateTime? from}) {
    Query<Map<String, dynamic>> q = _userRoot(userId).orderBy('createdAt', descending: true).limit(100);
    if (from != null) {
      q = q.where('createdAt', isGreaterThanOrEqualTo: from.toIso8601String());
    }
    return q.snapshots().map((s) => s.docs
        .map((d) => TeacherCheckin.fromJson(d.data(), d.id))
        .toList());
  }

  Future<TeacherSummary> computeAndStoreWeeklySummary(String userId, DateTime weekRef) async {
    final start = _startOfWeek(weekRef);
    final end = start.add(const Duration(days: 7));
    final snap = await _userRoot(userId)
        .where('createdAt', isGreaterThanOrEqualTo: start.toIso8601String())
        .where('createdAt', isLessThan: end.toIso8601String())
        .get();
    final items = snap.docs.map((d) => TeacherCheckin.fromJson(d.data(), d.id)).toList();
    final summary = _computeWeeklySummary(userId, start, items);
    final docId = _periodIdWeek(start);
    await _db.collection('teacher_wellbeing').doc(userId).collection('summaries').doc(docId).set(summary.toJson());
    return summary;
  }

  // Pure helper to compute summary from a list
  TeacherSummary _computeWeeklySummary(String userId, DateTime weekStart, List<TeacherCheckin> items) {
    if (items.isEmpty) {
      return TeacherSummary(
        id: _periodIdWeek(weekStart),
        userId: userId,
        period: 'week',
        moodAvg: 0,
        energyAvg: 0,
        checkinsCount: 0,
        topTags: const [],
      );
    }
    final moodAvg = items.map((e) => e.mood).reduce((a, b) => a + b) / items.length;
    final energyAvg = items.map((e) => e.energy).reduce((a, b) => a + b) / items.length;
    final tagCounts = <String, int>{};
    for (final c in items) {
      for (final t in c.tags) {
        tagCounts[t] = (tagCounts[t] ?? 0) + 1;
      }
    }
    final topTags = tagCounts.entries.toList()
      ..sort((a, b) => b.value.compareTo(a.value));
    final top = topTags.take(3).map((e) => e.key).toList();
    return TeacherSummary(
      id: _periodIdWeek(weekStart),
      userId: userId,
      period: 'week',
      moodAvg: double.parse(moodAvg.toStringAsFixed(2)),
      energyAvg: double.parse(energyAvg.toStringAsFixed(2)),
      checkinsCount: items.length,
      topTags: top,
    );
  }

  // Expose for tests
  TeacherSummary computeWeeklySummaryPure(String userId, DateTime weekRef, List<TeacherCheckin> items) {
    return _computeWeeklySummary(userId, _startOfWeek(weekRef), items);
  }

  DateTime _startOfWeek(DateTime d) {
    final weekday = d.weekday; // 1=Mon
    return DateTime(d.year, d.month, d.day).subtract(Duration(days: weekday - 1));
  }

  String _periodIdWeek(DateTime weekStart) {
    final weekNumber = ((weekStart.difference(DateTime(weekStart.year, 1, 1)).inDays) / 7).floor() + 1;
    final ww = weekNumber.toString().padLeft(2, '0');
    return '${weekStart.year}-W$ww';
  }
}

