import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class ProfesionalDetailScreen extends StatelessWidget {
  final String professionalId;

  const ProfesionalDetailScreen({super.key, required this.professionalId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Perfil del Profesional'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/profesionales'),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('ID del Profesional: $professionalId'),
            const SizedBox(height: 20),
            const Text('Perfil del Profesional - En desarrollo'),
          ],
        ),
      ),
    );
  }
}
