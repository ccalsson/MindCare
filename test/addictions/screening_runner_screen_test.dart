import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mindcare/features/addictions/models/screening_form.dart';
import 'package:mindcare/features/addictions/ui/screening_runner_screen.dart';

void main() {
  testWidgets('renders three types of questions and submits', (tester) async {
    final form = ScreeningForm(
      instrument: 'AUDIT',
      version: 1,
      questions: const [
        Question(id: 'q1', text: 'Single?', type: 'single', options: ['A', 'B']),
        Question(id: 'q2', text: 'Scale?', type: 'scale'),
        Question(id: 'q3', text: 'Multi?', type: 'multi', options: ['X', 'Y']),
      ],
      scoring: const {'type': 'sum'},
    );

    await tester.pumpWidget(MaterialApp(
      home: ScreeningRunnerScreen(instrument: 'AUDIT', version: 1, debugForm: form),
    ));

    expect(find.text('Single?'), findsOneWidget);
    expect(find.text('Scale?'), findsOneWidget);
    expect(find.text('Multi?'), findsOneWidget);

    await tester.tap(find.text('A'));
    await tester.pump();
    await tester.drag(find.byType(Slider).first, const Offset(50, 0));
    await tester.pump();
    await tester.tap(find.text('X'));
    await tester.pump();

    // Submit
    await tester.tap(find.text('Enviar'));
    await tester.pump();
  });
}

