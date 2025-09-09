import 'package:flutter/material.dart';

class RoutinesScreen extends StatelessWidget {
  const RoutinesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Rutinas')),
      body: ListView(
        children: const [
          ListTile(title: Text('Mañana: plan corto de 15 minutos')),
          ListTile(title: Text('Tarde: bloque de concentración')),
        ],
      ),
    );
  }
}

