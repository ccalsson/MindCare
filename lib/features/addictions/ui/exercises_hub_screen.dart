import 'package:flutter/material.dart';

class ExercisesHubScreen extends StatelessWidget {
  const ExercisesHubScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final items = const [
      {'id': 'breathing', 'title': 'Respiración 4-7-8'},
      {'id': 'mindfulness', 'title': 'Mindfulness 5 min'},
      {'id': 'journaling', 'title': 'Journaling'},
      {'id': 'music', 'title': 'Música relajante'},
    ];
    return Scaffold(
      appBar: AppBar(title: const Text('Ejercicios')),
      body: ListView.separated(
        itemCount: items.length,
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (context, i) {
          final e = items[i];
          return ListTile(
            title: Text(e['title']!),
            subtitle: const Text('Recomendado a diario'),
            onTap: () {
              // TODO(ccalsson): reproducir audios desde Storage /assets/audio
              ScaffoldMessenger.of(context)
                  .showSnackBar(SnackBar(content: Text('Abrir ${e['title']}')));
            },
          );
        },
      ),
    );
  }
}

