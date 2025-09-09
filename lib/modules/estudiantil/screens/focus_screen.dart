import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../student_provider.dart';

class FocusScreen extends StatefulWidget {
  const FocusScreen({super.key});

  @override
  State<FocusScreen> createState() => _FocusScreenState();
}

class _FocusScreenState extends State<FocusScreen> {
  int seconds = 25 * 60;
  bool running = false;
  @override
  Widget build(BuildContext context) {
    final minutes = (seconds ~/ 60).toString().padLeft(2, '0');
    final secs = (seconds % 60).toString().padLeft(2, '0');
    return Scaffold(
      appBar: AppBar(title: const Text('Enfoque')),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('$minutes:$secs', style: const TextStyle(fontSize: 42, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            FilledButton(
              onPressed: () async {
                setState(() => running = !running);
                if (running) {
                  while (running && seconds > 0) {
                    await Future.delayed(const Duration(seconds: 1));
                    if (!mounted || !running) break;
                    setState(() => seconds--);
                  }
                  if (seconds == 0 && mounted) {
                    context.read<StudentProvider>().completePomodoro();
                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Pomodoro completado')));
                  }
                }
              },
              child: Text(running ? 'Pausar' : 'Iniciar'),
            )
          ],
        ),
      ),
    );
  }
}

