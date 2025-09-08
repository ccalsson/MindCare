import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class EstudiantilScreen extends StatelessWidget {
  const EstudiantilScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Estudiantil'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/'),
        ),
      ),
      body: const Center(
        child: Text('Módulo Estudiantil - En desarrollo'),
      ),
    );
  }
}
