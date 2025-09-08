import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class BienestarScreen extends StatelessWidget {
  const BienestarScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bienestar'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/'),
        ),
      ),
      body: const Center(
        child: Text('Módulo de Bienestar - En desarrollo'),
      ),
    );
  }
}
