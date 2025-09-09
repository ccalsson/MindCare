import 'package:flutter/material.dart';

class AnxietyResourcesScreen extends StatelessWidget {
  const AnxietyResourcesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final items = const [
      {'title': 'Guía breve de respiración', 'url': 'https://example.com'},
      {'title': 'Mindfulness para principiantes', 'url': 'https://example.com'},
      {'title': 'Ansiedad: conceptos clave', 'url': 'https://example.com'},
    ];
    return Scaffold(
      appBar: AppBar(title: const Text('Recursos')),
      body: ListView.separated(
        itemCount: items.length,
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (_, i) => ListTile(
          leading: const Icon(Icons.link),
          title: Text(items[i]['title']!),
          onTap: () {},
        ),
      ),
    );
  }
}

