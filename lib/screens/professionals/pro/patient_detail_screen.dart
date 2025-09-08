import 'package:flutter/material.dart';

class PatientDetailScreen extends StatelessWidget {
  final String userId;
  const PatientDetailScreen({super.key, required this.userId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Historia del paciente')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Paciente: $userId'),
            const SizedBox(height: 8),
            const Text('Historial de citas, notas y diagnósticos aparecerán aquí.'),
            const Spacer(),
            Row(
              children: [
                OutlinedButton(onPressed: () => Navigator.pop(context), child: const Text('Volver')),
                const SizedBox(width: 8),
                FilledButton(onPressed: () {}, child: const Text('Ver historia clínica')),
              ],
            )
          ],
        ),
      ),
    );
  }
}

