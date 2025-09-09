import 'package:flutter/material.dart';

class HabitsScreen extends StatelessWidget {
  const HabitsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Hábitos')),
      body: ListView(
        children: const [
          ListTile(leading: Icon(Icons.check_circle_outline), title: Text('Meditar 5 minutos')),
          ListTile(leading: Icon(Icons.check_circle_outline), title: Text('Caminar 10 minutos')),
        ],
      ),
    );
  }
}

