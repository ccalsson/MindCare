import 'package:flutter/material.dart';
import '../services/routines_service.dart';
import '../models/routine.dart';

class RoutinesPage extends StatelessWidget {
  const RoutinesPage({super.key});

  @override
  Widget build(BuildContext context) {
    final service = RoutinesService();
    return Scaffold(
      appBar: AppBar(title: const Text('Rutinas express')),
      body: StreamBuilder<List<Routine>>(
        stream: service.watchRoutines(),
        builder: (context, snap) {
          if (!snap.hasData) return const Center(child: CircularProgressIndicator());
          final list = snap.data!;
          if (list.isEmpty) return const Center(child: Text('Pronto habrá rutinas disponibles'));
          return ListView.separated(
            itemCount: list.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, i) {
              final r = list[i];
              return ListTile(
                title: Text(r.title),
                subtitle: Text('${r.durationMin} min'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => _openRoutine(context, r),
              );
            },
          );
        },
      ),
    );
  }

  void _openRoutine(BuildContext context, Routine r) {
    showModalBottomSheet(
      context: context,
      showDragHandle: true,
      isScrollControlled: true,
      builder: (context) => Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(r.title, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            ...r.steps.map((s) => ListTile(leading: const Icon(Icons.check), title: Text(s))),
            if (r.audioUrl != null) ...[
              const SizedBox(height: 8),
              FilledButton.icon(onPressed: () {
                // TODO(ccalsson): reproducir audioUrl si existe
              }, icon: const Icon(Icons.play_arrow), label: const Text('Reproducir audio')),
            ],
            const SizedBox(height: 12),
          ],
        ),
      ),
    );
  }
}

