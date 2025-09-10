import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/modules/teacher_wellbeing/models/teacher_checkin.dart';
import 'package:mindcare/modules/teacher_wellbeing/services/checkin_service.dart';

void main() {
  test('computeWeeklySummaryPure averages and tags', () {
    final svc = CheckinService();
    final weekRef = DateTime(2025, 9, 8); // arbitrary
    final items = [
      TeacherCheckin(id: '1', userId: 'u', createdAt: weekRef, mood: 4, energy: 3, tags: const ['estrés', 'aula']),
      TeacherCheckin(id: '2', userId: 'u', createdAt: weekRef, mood: 2, energy: 5, tags: const ['sueño', 'aula']),
      TeacherCheckin(id: '3', userId: 'u', createdAt: weekRef, mood: 3, energy: 4, tags: const ['estrés']),
    ];
    final s = svc.computeWeeklySummaryPure('u', weekRef, items);
    expect(s.checkinsCount, 3);
    expect(s.moodAvg, closeTo(3.0, 0.01));
    expect(s.energyAvg, closeTo(4.0, 0.01));
    expect(s.topTags.first, anyOf('estrés', 'aula'));
  });
}

