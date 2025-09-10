import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/strings.dart';
import 'student_controller.dart';
import 'models.dart';

class StudentResourcesPage extends StatefulWidget {
  const StudentResourcesPage({super.key});

  @override
  State<StudentResourcesPage> createState() => _StudentResourcesPageState();
}

class _StudentResourcesPageState extends State<StudentResourcesPage> {
  String? _filter;
  final _tags = const ['Concentración', 'Memoria', 'Ansiedad exámenes', 'Organización'];

  @override
  Widget build(BuildContext context) {
    final controller = context.watch<StudentController>();
    final items = controller.resources.where((r) => _filter == null || r.tags.contains(_filter)).toList();
    return Scaffold(
      appBar: AppBar(title: const Text(S.resources)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Wrap(spacing: 8, runSpacing: 8, children: _tags.map((t) => FilterChip(
                label: Text(t),
                selected: _filter == t,
                onSelected: (v) => setState(() => _filter = v ? t : null),
              )).toList()),
          const SizedBox(height: 12),
          ...items.map((r) => _ResourceCard(res: r)).toList(),
        ],
      ),
    );
  }
}

class _ResourceCard extends StatelessWidget {
  final StudyResource res;
  const _ResourceCard({required this.res});
  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE7EAED))),
      child: ListTile(
        contentPadding: const EdgeInsets.all(12),
        title: Text(res.title),
        subtitle: Wrap(spacing: 6, children: [
          Text('${res.minutes} min', style: const TextStyle(color: Colors.black54)),
          ...res.tags.map((t) => Chip(label: Text(t), visualDensity: VisualDensity.compact)).toList(),
        ]),
        trailing: const Icon(Icons.chevron_right),
        onTap: () => _showDetail(context, res),
      ),
    );
  }

  void _showDetail(BuildContext context, StudyResource r) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
      builder: (ctx) => DraggableScrollableSheet(
        expand: false,
        initialChildSize: 0.7,
        minChildSize: 0.5,
        maxChildSize: 0.9,
        builder: (c, s) => Padding(
          padding: const EdgeInsets.all(16.0),
          child: ListView(
            controller: s,
            children: [
              Row(children: [
                Expanded(child: Text(r.title, style: Theme.of(context).textTheme.titleLarge)),
                IconButton(onPressed: () => Navigator.of(ctx).pop(), icon: const Icon(Icons.close)),
              ]),
              const SizedBox(height: 8),
              Text(r.subtitle, style: const TextStyle(color: Colors.black54)),
              const SizedBox(height: 12),
              const Text('Objetivo', style: TextStyle(fontWeight: FontWeight.w600)),
              Text(r.body),
              const SizedBox(height: 12),
              const Text('Pasos', style: TextStyle(fontWeight: FontWeight.w600)),
              const SizedBox(height: 6),
              const _Bullet(text: 'Respirar 1 minuto'),
              const _Bullet(text: 'Definir objetivo del bloque'),
              const _Bullet(text: 'Trabajar sin distracciones'),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () {
                  Navigator.of(ctx).pop();
                  _openWizard(context, r);
                },
                child: const Text('Aplicar ahora'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _openWizard(BuildContext context, StudyResource r) {
    Navigator.of(context).push(MaterialPageRoute(builder: (_) => _ResourceWizard(res: r)));
  }
}

class _Bullet extends StatelessWidget {
  final String text;
  const _Bullet({required this.text});
  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 2),
        child: Row(children: [const Text('• '), Expanded(child: Text(text))]),
      );
}

class _ResourceWizard extends StatefulWidget {
  final StudyResource res;
  const _ResourceWizard({required this.res});
  @override
  State<_ResourceWizard> createState() => _ResourceWizardState();
}

class _ResourceWizardState extends State<_ResourceWizard> {
  int step = 0;
  @override
  Widget build(BuildContext context) {
    final steps = [
      _StepCard(title: 'Respirá 1 min', content: 'Hacé una respiración guiada breve.'),
      _StepCard(title: 'Definí tu objetivo', content: 'Elegí un objetivo concreto y alcanzable.'),
      _StepCard(title: 'Comenzá el bloque', content: 'Trabajá sin distracciones durante ${widget.res.minutes} min.'),
    ];
    final isLast = step == steps.length - 1;
    return Scaffold(
      appBar: AppBar(title: Text(widget.res.title)),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(children: [
          Expanded(child: AnimatedSwitcher(duration: const Duration(milliseconds: 220), child: steps[step], switchInCurve: Curves.easeOut, switchOutCurve: Curves.easeIn)),
          Row(children: [
            if (step > 0) OutlinedButton(onPressed: () => setState(() => step -= 1), child: const Text('Atrás')),
            const Spacer(),
            FilledButton(onPressed: () => setState(() => step = isLast ? step : step + 1), child: Text(isLast ? 'Finalizar' : 'Siguiente')),
          ])
        ]),
      ),
    );
  }
}

class _StepCard extends StatelessWidget {
  final String title;
  final String content;
  const _StepCard({required this.title, required this.content});
  @override
  Widget build(BuildContext context) {
    return Card(
      key: ValueKey(title),
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12), side: const BorderSide(color: Color(0xFFE7EAED))),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(title, style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 8),
          Text(content),
        ]),
      ),
    );
  }
}

