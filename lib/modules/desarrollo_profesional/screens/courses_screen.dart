import 'package:flutter/material.dart';

class CoursesScreen extends StatelessWidget {
  const CoursesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Cursos')),
      body: ListView(
        children: const [
          ListTile(leading: Icon(Icons.play_circle_outline), title: Text('Mindfulness para principiantes')),
          ListTile(leading: Icon(Icons.play_circle_outline), title: Text('Gestión del estrés')),
        ],
      ),
    );
  }
}

