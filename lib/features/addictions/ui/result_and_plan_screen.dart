import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:flutter/material.dart';
import '../models/plan.dart';
import '../models/screening_result.dart';
import '../data/plans_repo.dart';

class ResultAndPlanScreen extends StatefulWidget {
  final ScreeningResult result;
  const ResultAndPlanScreen({super.key, required this.result});

  @override
  State<ResultAndPlanScreen> createState() => _ResultAndPlanScreenState();
}

class _ResultAndPlanScreenState extends State<ResultAndPlanScreen> {
  late final PlansRepo _plansRepo;

  @override
  void initState() {
    super.initState();
    _plansRepo = PlansRepo(db: FirebaseFirestore.instance);
  }

  @override
  Widget build(BuildContext context) {
    final r = widget.result;
    final isHigh = r.riskBand == 'high';
    return Scaffold(
      appBar: AppBar(title: const Text('Resultados')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${r.instrument} v${r.version}', style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            Text('Puntaje: ${r.score} — Riesgo: ${r.riskBand}'),
            const SizedBox(height: 16),
            const Text('Plan sugerido:'),
            const SizedBox(height: 8),
            _SuggestedPlan(isHigh: isHigh, onStart: _startPlan),
            const Spacer(),
            if (isHigh)
              FilledButton(
                onPressed: () {
                  showModalBottomSheet(
                    context: context,
                    showDragHandle: true,
                    builder: (_) => const Padding(
                      padding: EdgeInsets.all(8.0),
                      child: SizedBox(height: 360, child: Text('Ayuda inmediata (abrir desde /addictions/help)')),
                    ),
                  );
                },
                child: const Text('Necesito ayuda ahora'),
              ),
            const SizedBox(height: 8),
            OutlinedButton(
              onPressed: () => Navigator.of(context).pushNamed('/profesionales'),
              child: const Text('Derivación profesional'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _startPlan(List<PlanItem> items) async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;
    final plan = Plan(status: 'active', items: items, nextCheckinAt: DateTime.now().add(const Duration(days: 1)));
    await _plansRepo.createOrUpdate(uid, plan);
    await FirebaseAnalytics.instance.logEvent(name: 'plan_started');
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Plan iniciado')));
      // ignore: use_build_context_synchronously
      Navigator.of(context).pushReplacementNamed('/addictions/checkin');
    }
  }
}

class _SuggestedPlan extends StatelessWidget {
  final bool isHigh;
  final Future<void> Function(List<PlanItem>) onStart;
  const _SuggestedPlan({required this.isHigh, required this.onStart});

  @override
  Widget build(BuildContext context) {
    final items = <PlanItem>[
      const PlanItem(id: 'breathing', title: 'Respiración 4-7-8', type: 'exercise', cadence: 'daily'),
      const PlanItem(id: 'mindfulness', title: 'Mindfulness 5 min', type: 'exercise', cadence: 'daily'),
      const PlanItem(id: 'journal', title: 'Registro diario', type: 'habit', cadence: 'daily'),
      if (isHigh) const PlanItem(id: 'session', title: 'Sesión con profesional', type: 'session', cadence: 'weekly'),
    ];
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ...items.map((e) => Row(
                  children: [
                    const Icon(Icons.check_circle_outline, size: 18),
                    const SizedBox(width: 8),
                    Expanded(child: Text(e.title)),
                  ],
                )),
            const SizedBox(height: 8),
            FilledButton(onPressed: () => onStart(items), child: const Text('Comenzar plan')),
          ],
        ),
      ),
    );
  }
}
