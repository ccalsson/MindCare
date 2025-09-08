import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:mindcare/screens/professionals_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  Future<void> _logout(BuildContext context) async {
    await FirebaseAuth.instance.signOut();
    if (context.mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Sesión cerrada')));
    }
  }

  @override
  Widget build(BuildContext context) {
    final user = FirebaseAuth.instance.currentUser;
    return Scaffold(
      appBar: AppBar(
        title: const Text('MindCare'),
        actions: [
          IconButton(
            onPressed: () => _logout(context),
            icon: const Icon(Icons.logout),
            tooltip: 'Salir',
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.self_improvement, size: 72),
            const SizedBox(height: 12),
            Text('Hola ${user?.email ?? 'usuario'}',
                style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 24),
            const Text('HomeScreen listo. Conectá tus pantallas reales acá.'),
            const SizedBox(height: 24),
            FilledButton.icon(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => const ProfessionalsScreen(),
                  ),
                );
              },
              icon: const Icon(Icons.medical_services_outlined),
              label: const Text('Ver profesionales (Supabase)'),
            )
          ],
        ),
      ),
    );
  }
}
