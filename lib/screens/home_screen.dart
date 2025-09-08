import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MindCare - Inicio'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Bienvenido a MindCare',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            const Text('Tu compañero de bienestar mental con IA'),
            const SizedBox(height: 40),
            ElevatedButton(
              onPressed: () => context.go('/bienestar'),
              child: const Text('Módulo Bienestar'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => context.go('/paywall'),
              child: const Text('Ver Planes'),
            ),
          ],
        ),
      ),
    );
  }
}
