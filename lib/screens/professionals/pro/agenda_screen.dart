import 'package:flutter/material.dart';

class AgendaScreen extends StatelessWidget {
  const AgendaScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.event, size: 48),
            const SizedBox(height: 8),
            const Text('Acá verás tu agenda y calendario'),
            const SizedBox(height: 12),
            OutlinedButton(onPressed: () {}, child: const Text('Volver')),
          ],
        ),
      ),
    );
  }
}

