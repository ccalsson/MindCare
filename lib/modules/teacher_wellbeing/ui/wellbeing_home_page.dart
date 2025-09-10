import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../state/teacher_wellbeing_controller.dart';
import '../widgets/mood_energy_chart.dart';
import '../widgets/wellbeing_card.dart';
import '../services/ai_recommendations_service.dart';
import 'checkin_page.dart';
import 'routines_page.dart';
import 'resources_page.dart';

class WellbeingHomePage extends StatelessWidget {
  const WellbeingHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = context.read<TeacherWellbeingController>();
    controller.loadWeeklySummary();
    final uid = FirebaseAuth.instance.currentUser?.uid;
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mi Bienestar Docente'),
        backgroundColor: Colors.transparent,
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [Color(0xFF2E86E6), Color(0xFF25D366)],
            ),
          ),
        ),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Semantics(
                label: 'Gráfico semanal de ánimo y energía',
                child: StreamBuilder(
                  stream: controller.watchRecent(),
                  builder: (context, snap) {
                    final list = (snap.data ?? const []) as List;
                    if (list.isEmpty) {
                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Comencemos con tu primer check-in', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
                          const SizedBox(height: 8),
                          FilledButton(
                            onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CheckinPage())),
                            child: const Text('Check-in rápido'),
                          ),
                        ],
                      );
                    }
                    final moods = list.reversed.map((e) => e.mood as int).take(7).toList();
                    final energies = list.reversed.map((e) => e.energy as int).take(7).toList();
                    return Card(
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Últimos 7 días'),
                            const SizedBox(height: 8),
                            MoodEnergyChart(moods: moods, energies: energies),
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 16),
              WellbeingCard(
                title: 'Check-in rápido',
                subtitle: '2 sliders y listo',
                icon: Icons.fiber_manual_record,
                onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const CheckinPage())),
              ),
              WellbeingCard(
                title: 'Rutinas express',
                subtitle: 'Pausa guiada de 3–10 min',
                icon: Icons.self_improvement,
                onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RoutinesPage())),
              ),
              WellbeingCard(
                title: 'Recursos',
                subtitle: 'Artículos, audios y videos',
                icon: Icons.menu_book_outlined,
                onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const ResourcesPage())),
              ),
              const SizedBox(height: 8),
              FutureBuilder<List<String>>(
                future: AiRecommendationsService().suggestSelfCare(3, 3, const []),
                builder: (context, snap) {
                  final recs = snap.data ?? const <String>[];
                  if (recs.isEmpty) return const SizedBox.shrink();
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(12),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('Recomendaciones'),
                          const SizedBox(height: 8),
                          ...recs.map((r) => Row(
                                children: [
                                  const Icon(Icons.check_circle_outline, size: 18, color: Color(0xFF25D366)),
                                  const SizedBox(width: 8),
                                  Expanded(child: Text(r)),
                                ],
                              )),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
