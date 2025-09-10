import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/modules/teacher_wellbeing/models/teacher_checkin.dart';
import 'package:mindcare/modules/teacher_wellbeing/models/teacher_summary.dart';
import 'package:mindcare/modules/teacher_wellbeing/models/routine.dart';
import 'package:mindcare/modules/teacher_wellbeing/models/wellbeing_resource.dart';

void main() {
  test('TeacherCheckin to/from json', () {
    final c = TeacherCheckin(id: 'id', userId: 'u', createdAt: DateTime.parse('2025-09-09T00:00:00Z'), mood: 4, energy: 3, tags: const ['a']);
    final j = c.toJson();
    final c2 = TeacherCheckin.fromJson(j, 'id');
    expect(c2.userId, 'u');
    expect(c2.mood, 4);
  });

  test('TeacherSummary to/from json', () {
    final s = TeacherSummary(id: '2025-W37', userId: 'u', period: 'week', moodAvg: 3.5, energyAvg: 4.0, checkinsCount: 3, topTags: const ['estres']);
    final j = s.toJson();
    final s2 = TeacherSummary.fromJson(j, '2025-W37');
    expect(s2.period, 'week');
    expect(s2.moodAvg, 3.5);
  });

  test('Routine to/from json', () {
    final r = Routine(id: 'r', title: 't', durationMin: 3, steps: const ['s1']);
    final j = r.toJson();
    final r2 = Routine.fromJson(j, 'r');
    expect(r2.title, 't');
  });

  test('WellbeingResource to/from json', () {
    final w = WellbeingResource(id: 'w', title: 't', kind: 'articulo', durationMin: 5, topics: const ['organizacion'], contentUrl: 'https://x', description: 'd');
    final j = w.toJson();
    final w2 = WellbeingResource.fromJson(j, 'w');
    expect(w2.kind, 'articulo');
  });
}

