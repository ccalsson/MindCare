import 'package:flutter/material.dart';
import '../anxiety_content_service.dart';

class TechniquesScreen extends StatelessWidget {
  const TechniquesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final list = AnxietyContentService().techniques();
    return Scaffold(
      appBar: AppBar(title: const Text('Técnicas')),
      body: ListView.separated(
        itemCount: list.length,
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (_, i) => ListTile(
          leading: const Icon(Icons.check_circle_outline),
          title: Text(list[i]),
          onTap: () {},
        ),
      ),
    );
  }
}

