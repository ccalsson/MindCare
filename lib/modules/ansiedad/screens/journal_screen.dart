import 'package:flutter/material.dart';

class JournalScreen extends StatelessWidget {
  const JournalScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final c = TextEditingController();
    return Scaffold(
      appBar: AppBar(title: const Text('Diario')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: c,
              maxLines: 8,
              decoration: const InputDecoration(
                hintText: 'Escribe cómo te sentís hoy...',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                OutlinedButton(onPressed: () => Navigator.pop(context), child: const Text('Volver')),
                const SizedBox(width: 8),
                FilledButton(onPressed: () {/* TODO: Guardar */}, child: const Text('Guardar')),
              ],
            )
          ],
        ),
      ),
    );
  }
}

