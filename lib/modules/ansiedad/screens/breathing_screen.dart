import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../anxiety_provider.dart';

class BreathingScreen extends StatefulWidget {
  const BreathingScreen({super.key});

  @override
  State<BreathingScreen> createState() => _BreathingScreenState();
}

class _BreathingScreenState extends State<BreathingScreen> with SingleTickerProviderStateMixin {
  late final AnimationController _ctrl;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(vsync: this, duration: const Duration(seconds: 4))..repeat(reverse: true);
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Respiración')),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ScaleTransition(
              scale: Tween<double>(begin: 0.7, end: 1.0).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeInOut)),
              child: const CircleAvatar(radius: 60, backgroundColor: Color(0xFF2E86E6)),
            ),
            const SizedBox(height: 16),
            const Text('Inhala · Exhala', style: TextStyle(fontSize: 16)),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: () => context.read<AnxietyProvider>().completeOne(),
              child: const Text('Marcar como completado'),
            )
          ],
        ),
      ),
    );
  }
}

