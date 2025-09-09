import 'package:flutter/material.dart';

class StudentResourcesScreen extends StatelessWidget {
  const StudentResourcesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Recursos')),
      body: ListView(
        children: const [
          ListTile(leading: Icon(Icons.link), title: Text('Técnicas de estudio')), 
          ListTile(leading: Icon(Icons.link), title: Text('Hábitos y descanso')),
        ],
      ),
    );
  }
}

