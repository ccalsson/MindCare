import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AddictionsIntroScreen extends StatelessWidget {
  const AddictionsIntroScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final instruments = const [
      {'id': 'AUDIT', 'title': 'Alcohol'},
      {'id': 'DAST', 'title': 'Drogas'},
      {'id': 'IGDS9', 'title': 'Juego/Tech'},
      {'id': 'ASSIST', 'title': 'ASSIST (OMS)'},
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Bienestar y Adicciones')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text(
            'Elegí un área y realizá una evaluación inicial. Podés empezar con AUDIT (alcohol).',
          ),
          const SizedBox(height: 16),
          Wrap(
            spacing: 12,
            runSpacing: 12,
            children: instruments
                .map((m) => ChoiceChip(
                      label: Text(m['title']!),
                      selected: false,
                      onSelected: (_) => context.push('/addictions/screening/${m['id']}/1'),
                    ))
                .toList(),
          ),
          const SizedBox(height: 24),
          FilledButton(
            onPressed: () => context.push('/addictions/screening/AUDIT/1'),
            child: const Text('Evaluarme (AUDIT)'),
          ),
          const SizedBox(height: 8),
          OutlinedButton(
            onPressed: () => context.push('/addictions/checkin'),
            child: const Text('Check-in diario'),
          ),
        ],
      ),
    );
  }
}

