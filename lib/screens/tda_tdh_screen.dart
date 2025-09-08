import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class TdaTdhScreen extends StatelessWidget {
  const TdaTdhScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('TDA/TDAH'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/'),
        ),
      ),
      body: const Center(
        child: Text('Módulo TDA/TDAH - En desarrollo'),
      ),
    );
  }
}
