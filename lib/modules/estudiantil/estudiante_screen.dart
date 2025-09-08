import 'package:flutter/material.dart';

class EstudianteScreen extends StatelessWidget {
  const EstudianteScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Estudiante')),
      body: const Center(child: Text('Contenido para Estudiantes')),
    );
  }
}

