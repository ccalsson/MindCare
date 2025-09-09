import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../tda_tdh_provider.dart';

class TasksScreen extends StatelessWidget {
  const TasksScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final count = context.watch<TdaTdhProvider>().tasks;
    return Scaffold(
      appBar: AppBar(title: const Text('Tareas')),
      body: Column(
        children: [
          ListTile(title: Text('Tareas: $count')), 
          Expanded(
            child: ListView.builder(
              itemCount: count,
              itemBuilder: (_, i) => ListTile(leading: const Icon(Icons.task_alt), title: Text('Tarea #${i + 1}')),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: FilledButton.icon(
              onPressed: () => context.read<TdaTdhProvider>().addTask(),
              icon: const Icon(Icons.add),
              label: const Text('Agregar'),
            ),
          )
        ],
      ),
    );
  }
}

