import 'package:flutter/material.dart';
import '../../../widgets/gradient_scaffold.dart';
import '../../../widgets/section_header.dart';
import 'goals_screen.dart';
import 'habits_screen.dart';
import 'courses_screen.dart';

class PersonalDevOverviewScreen extends StatelessWidget {
  const PersonalDevOverviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return GradientScaffold(
      appBar: AppBar(title: const Text('Desarrollo Personal')),
      body: ListView(
        children: [
          const SectionHeader(title: 'Crecé día a día'),
          const SizedBox(height: 8),
          _tile(context, Icons.flag_outlined, 'Metas', const GoalsScreen()),
          _tile(context, Icons.repeat, 'Hábitos', const HabitsScreen()),
          _tile(context, Icons.school_outlined, 'Cursos', const CoursesScreen()),
        ],
      ),
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, Widget screen) {
    return Card(child: ListTile(leading: Icon(icon, color: const Color(0xFF2E86E6)), title: Text(title), trailing: const Icon(Icons.chevron_right), onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => screen))));
  }
}

