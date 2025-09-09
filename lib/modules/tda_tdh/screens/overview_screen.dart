import 'package:flutter/material.dart';
import '../../../widgets/gradient_scaffold.dart';
import '../../../widgets/section_header.dart';
import 'routines_screen.dart';
import 'tasks_screen.dart';

class TdaTdhOverviewScreen extends StatelessWidget {
  const TdaTdhOverviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return GradientScaffold(
      appBar: AppBar(title: const Text('TDA/TDH')),
      body: ListView(
        children: [
          const SectionHeader(title: 'Herramientas prácticas'),
          const SizedBox(height: 8),
          _tile(context, Icons.repeat, 'Rutinas', const RoutinesScreen()),
          _tile(context, Icons.checklist, 'Tareas', const TasksScreen()),
        ],
      ),
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, Widget screen) {
    return Card(child: ListTile(leading: Icon(icon, color: const Color(0xFF25D366)), title: Text(title), trailing: const Icon(Icons.chevron_right), onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => screen))));
  }
}

