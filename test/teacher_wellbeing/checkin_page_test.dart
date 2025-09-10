import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/modules/teacher_wellbeing/state/teacher_wellbeing_controller.dart';
import 'package:mindcare/modules/teacher_wellbeing/ui/checkin_page.dart';
import 'package:provider/provider.dart';

class FakeController extends TeacherWellbeingController {
  bool saved = false;
  @override
  Future<void> doCheckin({required int mood, required int energy, required List<String> tags, String? note}) async {
    saved = true;
  }
}

void main() {
  testWidgets('CheckinPage saves and shows SnackBar', (tester) async {
    final fake = FakeController();
    await tester.pumpWidget(
      MaterialApp(
        home: ChangeNotifierProvider<TeacherWellbeingController>.value(
          value: fake,
          child: const CheckinPage(),
        ),
      ),
    );

    await tester.tap(find.text('Guardar check-in'));
    await tester.pump();
    expect(fake.saved, true);
  });
}

