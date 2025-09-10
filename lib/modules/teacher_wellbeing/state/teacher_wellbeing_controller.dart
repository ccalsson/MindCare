import 'package:flutter/foundation.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/teacher_checkin.dart';
import '../models/teacher_summary.dart';
import '../services/checkin_service.dart';

class TeacherWellbeingController extends ChangeNotifier {
  final CheckinService _checkinService;
  TeacherSummary? currentSummary;
  List<TeacherCheckin> recent = const [];

  TeacherWellbeingController({CheckinService? checkinService})
      : _checkinService = checkinService ?? CheckinService();

  String? get _uid => FirebaseAuth.instance.currentUser?.uid;

  Stream<List<TeacherCheckin>> watchRecent() {
    final uid = _uid;
    if (uid == null) return const Stream.empty();
    final from = DateTime.now().subtract(const Duration(days: 7));
    return _checkinService.watchCheckins(uid, from: from);
  }

  Future<void> doCheckin({required int mood, required int energy, required List<String> tags, String? note}) async {
    final uid = _uid;
    if (uid == null) throw StateError('No autenticado');
    if (note != null && note.length > 200) {
      throw StateError('Nota demasiado larga (máx 200)');
    }
    final c = TeacherCheckin(
      id: 'auto',
      userId: uid,
      createdAt: DateTime.now(),
      mood: mood,
      energy: energy,
      tags: tags,
      note: note,
    );
    await _checkinService.createCheckin(c);
    notifyListeners();
  }

  Future<void> loadWeeklySummary() async {
    final uid = _uid;
    if (uid == null) return;
    currentSummary = await _checkinService.computeAndStoreWeeklySummary(uid, DateTime.now());
    notifyListeners();
  }
}

