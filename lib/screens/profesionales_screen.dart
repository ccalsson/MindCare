import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class ProfesionalesScreen extends StatelessWidget {
  const ProfesionalesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profesionales'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/'),
        ),
      ),
      body: const Center(
        child: Text('Directorio de Profesionales - En desarrollo'),
      ),
    );
  }
}
