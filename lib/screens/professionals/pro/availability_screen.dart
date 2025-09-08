import 'package:flutter/material.dart';

class AvailabilityEditorScreen extends StatelessWidget {
  const AvailabilityEditorScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Editor de disponibilidad semanal'),
          const SizedBox(height: 12),
          Expanded(
            child: ListView.builder(
              itemCount: 5,
              itemBuilder: (_, i) => Card(
                child: ListTile(
                  leading: const Icon(Icons.schedule),
                  title: Text('Día ${i + 1}'),
                  subtitle: const Text('10:00 - 12:00, 14:00 - 18:00'),
                  trailing: const Icon(Icons.edit),
                ),
              ),
            ),
          ),
          Row(
            children: [
              OutlinedButton(onPressed: () {}, child: const Text('Volver')),
              const SizedBox(width: 8),
              FilledButton(onPressed: () {}, child: const Text('Guardar')),
            ],
          )
        ],
      ),
    );
  }
}

