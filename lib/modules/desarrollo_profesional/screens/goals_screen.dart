import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:mindcare/modules/desarrollo_personal/personal_dev_provider.dart';

class GoalsScreen extends StatelessWidget {
  const GoalsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final count = context.watch<PersonalDevProvider>().goals;
    return Scaffold(
      appBar: AppBar(title: const Text('Metas')),
      body: Column(
        children: [
          ListTile(title: Text('Metas activas: $count')),
          Expanded(
            child: ListView.builder(
              itemCount: count,
              itemBuilder: (_, i) => ListTile(leading: const Icon(Icons.flag), title: Text('Meta #${i + 1}')),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: FilledButton.icon(onPressed: () => context.read<PersonalDevProvider>().addGoal(), icon: const Icon(Icons.add), label: const Text('Agregar meta')),
          )
        ],
      ),
    );
  }
}
