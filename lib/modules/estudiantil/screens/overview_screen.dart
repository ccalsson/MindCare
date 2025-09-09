import 'package:flutter/material.dart';
import '../../../widgets/gradient_scaffold.dart';
import '../../../widgets/section_header.dart';
import 'planner_screen.dart';
import 'focus_screen.dart';
import 'resources_screen.dart';

class StudentOverviewScreen extends StatelessWidget {
  const StudentOverviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return GradientScaffold(
      appBar: AppBar(title: const Text('Estudiante')),
      body: ListView(
        children: [
          const SectionHeader(title: 'Organizá tu estudio'),
          const SizedBox(height: 8),
          _tile(context, Icons.event_note, 'Planificador', const PlannerScreen()),
          _tile(context, Icons.timer, 'Enfoque (Pomodoro)', const FocusScreen()),
          _tile(context, Icons.library_books_outlined, 'Recursos', const StudentResourcesScreen()),
        ],
      ),
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, Widget screen) {
    return Card(child: ListTile(leading: Icon(icon, color: const Color(0xFF2E86E6)), title: Text(title), trailing: const Icon(Icons.chevron_right), onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => screen))));
  }
}

