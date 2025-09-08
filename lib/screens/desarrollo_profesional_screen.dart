import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class DesarrolloProfesionalScreen extends StatelessWidget {
  const DesarrolloProfesionalScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Desarrollo Profesional'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/'),
        ),
      ),
      body: const Center(
        child: Text('Módulo de Desarrollo Profesional - En desarrollo'),
      ),
    );
  }
}
