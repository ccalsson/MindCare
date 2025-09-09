import 'package:flutter/material.dart';

class PlannerScreen extends StatelessWidget {
  const PlannerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Planificador')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: const [
          ListTile(leading: Icon(Icons.book), title: Text('Matemática: repaso de integrales')),
          ListTile(leading: Icon(Icons.book), title: Text('Historia: lectura cap. 3')),
        ],
      ),
    );
  }
}

