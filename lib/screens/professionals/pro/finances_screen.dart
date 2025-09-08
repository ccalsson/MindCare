import 'package:flutter/material.dart';

class FinancesScreen extends StatelessWidget {
  const FinancesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Finanzas'),
          const SizedBox(height: 8),
          const Text('Ingresos estimados: —'),
          const Text('Próximas liquidaciones: —'),
          const Spacer(),
          OutlinedButton(onPressed: () {}, child: const Text('Volver')),
        ],
      ),
    );
  }
}

