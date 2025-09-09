import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../widgets/gradient_scaffold.dart';
import '../../../widgets/section_header.dart';
import '../anxiety_provider.dart';
import 'techniques_screen.dart';
import 'breathing_screen.dart';
import 'journal_screen.dart';
import 'resources_screen.dart';
import 'ai_coach_screen.dart';

class AnxietyOverviewScreen extends StatelessWidget {
  const AnxietyOverviewScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final done = context.watch<AnxietyProvider>().completedExercises;
    return GradientScaffold(
      appBar: AppBar(title: const Text('Manejo de Ansiedad')),
      body: ListView(
        children: [
          const SectionHeader(title: 'Tu progreso', subtitle: 'Ejercicios completados'),
          const SizedBox(height: 8),
          Card(
            child: ListTile(
              leading: const Icon(Icons.insights),
              title: Text('Completados: $done'),
              subtitle: const Text('Practicá a diario para mejores resultados'),
            ),
          ),
          const SizedBox(height: 16),
          const SectionHeader(title: 'Herramientas'),
          const SizedBox(height: 8),
          _tile(context, Icons.self_improvement, 'Técnicas', const TechniquesScreen()),
          _tile(context, Icons.air, 'Respiración', const BreathingScreen()),
          _tile(context, Icons.menu_book_outlined, 'Diario', const JournalScreen()),
          _tile(context, Icons.library_books_outlined, 'Recursos', const AnxietyResourcesScreen()),
          _tile(context, Icons.smart_toy_outlined, 'Coach IA', const AiCoachScreen()),
        ],
      ),
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, Widget screen) {
    return Card(
      child: ListTile(
        leading: Icon(icon, color: const Color(0xFF2E86E6)),
        title: Text(title),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => screen)),
      ),
    );
  }
}

