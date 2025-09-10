import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/strings.dart';
import 'student_controller.dart';
import 'models.dart';

class StudentRoutinesPage extends StatelessWidget {
  const StudentRoutinesPage({super.key});

  @override
  Widget build(BuildContext context) {
    final c = context.watch<StudentController>();
    return Scaffold(
      appBar: AppBar(title: const Text(S.routines), actions: [
        IconButton(onPressed: () => _showCreateRoutine(context), icon: const Icon(Icons.add))
      ]),
      body: GridView.builder(
        padding: const EdgeInsets.all(16),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 2, crossAxisSpacing: 12, mainAxisSpacing: 12, childAspectRatio: 1.2),
        itemCount: c.myRoutines.length,
        itemBuilder: (ctx, i) => _RoutineCard(routine: c.myRoutines[i]),
      ),
    );
  }

  void _showCreateRoutine(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
      builder: (ctx) => const _CreateRoutineSheet(),
    );
  }
}

class _RoutineCard extends StatefulWidget {
  final StudyRoutine routine;
  const _RoutineCard({required this.routine});
  @override
  State<_RoutineCard> createState() => _RoutineCardState();
}

class _RoutineCardState extends State<_RoutineCard> {
  int idx = -1; // -1 idle
  @override
  Widget build(BuildContext context) {
    final r = widget.routine;
    final active = idx >= 0 && idx < r.steps.length;
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE7EAED))),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(r.name, style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 8),
          Expanded(
            child: AnimatedSwitcher(
              duration: const Duration(milliseconds: 220),
              child: active
                  ? _PhaseIndicator(step: r.steps[idx])
                  : Wrap(spacing: 6, children: r.steps.map((s) => Chip(label: Text(_label(s)), visualDensity: VisualDensity.compact)).toList()),
            ),
          ),
          const SizedBox(height: 8),
          Row(children: [
            FilledButton(
              onPressed: () async {
                for (var i = 0; i < r.steps.length; i++) {
                  if (!mounted) return;
                  setState(() => idx = i);
                  await Future.delayed(const Duration(milliseconds: 300)); // mock timer step
                }
                if (!mounted) return;
                setState(() => idx = -1);
              },
              child: const Text('Iniciar'),
            ),
          ])
        ]),
      ),
    );
  }

  String _label(RoutineStep s) {
    switch (s.type) {
      case 'breathe':
        return 'Respirar ${s.minutes}\'';
      case 'focus':
        return 'Foco ${s.minutes}\'';
      case 'break':
        return 'Descanso ${s.minutes}\'';
      case 'check':
        return 'Checklist ${s.minutes}\'';
      default:
        return '${s.type} ${s.minutes}\'';
    }
  }
}

class _PhaseIndicator extends StatelessWidget {
  final RoutineStep step;
  const _PhaseIndicator({required this.step});
  @override
  Widget build(BuildContext context) {
    IconData icon = Icons.timer;
    Color color = Theme.of(context).colorScheme.primary;
    switch (step.type) {
      case 'breathe':
        icon = Icons.self_improvement;
        color = Colors.teal;
        break;
      case 'focus':
        icon = Icons.center_focus_strong;
        color = Colors.indigo;
        break;
      case 'break':
        icon = Icons.free_breakfast;
        color = Colors.orange;
        break;
      case 'check':
        icon = Icons.checklist;
        color = Colors.green;
        break;
    }
    return Column(
      key: ValueKey(step.type + step.minutes.toString()),
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(children: [Icon(icon, color: color), const SizedBox(width: 8), Text('Fase: ${step.type}')]),
        const SizedBox(height: 8),
        LinearProgressIndicator(value: null, color: color),
        const SizedBox(height: 8),
        Text('Duración ${step.minutes} min (mock)')
      ],
    );
  }
}

class _CreateRoutineSheet extends StatefulWidget {
  const _CreateRoutineSheet();
  @override
  State<_CreateRoutineSheet> createState() => _CreateRoutineSheetState();
}

class _CreateRoutineSheetState extends State<_CreateRoutineSheet> {
  final nameCtrl = TextEditingController();
  final List<RoutineStep> steps = [];

  @override
  Widget build(BuildContext context) {
    final c = context.read<StudentController>();
    return Padding(
      padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(mainAxisSize: MainAxisSize.min, crossAxisAlignment: CrossAxisAlignment.start, children: [
            const Text('Crear rutina', style: TextStyle(fontWeight: FontWeight.w600)),
            const SizedBox(height: 12),
            TextField(controller: nameCtrl, decoration: const InputDecoration(labelText: 'Nombre')),
            const SizedBox(height: 12),
            const Text('Agregar paso'),
            const SizedBox(height: 8),
            Wrap(spacing: 8, children: [
              OutlinedButton(onPressed: () => setState(() => steps.add(const RoutineStep(type: 'breathe', minutes: 1))), child: const Text('Respirar 1\'')),
              OutlinedButton(onPressed: () => setState(() => steps.add(const RoutineStep(type: 'focus', minutes: 20))), child: const Text('Foco 20\'')),
              OutlinedButton(onPressed: () => setState(() => steps.add(const RoutineStep(type: 'break', minutes: 5))), child: const Text('Descanso 5\'')),
            ]),
            const SizedBox(height: 12),
            ...steps.map((s) => ListTile(
                  dense: true,
                  visualDensity: VisualDensity.compact,
                  leading: const Icon(Icons.drag_indicator),
                  title: Text('${s.type} - ${s.minutes} min'),
                  trailing: IconButton(icon: const Icon(Icons.close), onPressed: () => setState(() => steps.remove(s))),
                )),
            const SizedBox(height: 12),
            Row(children: [
              const Spacer(),
              FilledButton(
                onPressed: () {
                  if (nameCtrl.text.trim().isEmpty || steps.isEmpty) return;
                  final r = StudyRoutine(id: DateTime.now().millisecondsSinceEpoch.toString(), name: nameCtrl.text.trim(), steps: List.of(steps));
                  c.addRoutine(r);
                  Navigator.of(context).pop();
                },
                child: const Text(S.saveRoutine),
              ),
            ])
          ]),
        ),
      ),
    );
  }
}

