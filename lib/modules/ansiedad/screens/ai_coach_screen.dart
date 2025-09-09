import 'package:flutter/material.dart';

class AiCoachScreen extends StatelessWidget {
  const AiCoachScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final c = TextEditingController();
    return Scaffold(
      appBar: AppBar(title: const Text('Coach IA')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Expanded(
              child: ListView(
                children: const [
                  ListTile(title: Text('IA: ¿En qué puedo ayudarte hoy?')),
                ],
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: c,
                    decoration: const InputDecoration(hintText: 'Escribe tu mensaje...', border: OutlineInputBorder()),
                  ),
                ),
                const SizedBox(width: 8),
                FilledButton(onPressed: () {/* TODO: Integrar IA */}, child: const Icon(Icons.send))
              ],
            )
          ],
        ),
      ),
    );
  }
}

