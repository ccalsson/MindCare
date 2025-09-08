import 'package:flutter/material.dart';

class DesarrolloPersonalScreen extends StatelessWidget {
  const DesarrolloPersonalScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Desarrollo Personal')),
      body: const Center(child: Text('Contenido de Desarrollo Personal')),
    );
  }
}

