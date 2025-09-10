import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/features/addictions/data/screening_repo.dart';
import 'package:mindcare/features/addictions/models/screening_form.dart';

void main() {
  group('AUDIT scoring', () {
    final repo = ScreeningRepo();
    test('computeScore sums numeric answers', () {
      final answers = [
        const Answer(qId: 'q1', value: 2),
        const Answer(qId: 'q2', value: 3),
        const Answer(qId: 'q3', value: 1),
      ];
      expect(repo.computeScore('AUDIT', answers), 6);
    });

    test('bandFromScore thresholds', () {
      expect(repo.bandFromScore('AUDIT', 6), 'low');
      expect(repo.bandFromScore('AUDIT', 12), 'moderate');
      expect(repo.bandFromScore('AUDIT', 20), 'high');
    });
  });
}

