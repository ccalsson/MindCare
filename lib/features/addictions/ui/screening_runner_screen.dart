import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import '../../addictions/data/screening_form_repo.dart';
import '../../addictions/data/screening_repo.dart';
import '../models/screening_form.dart';
import 'result_and_plan_screen.dart';

class ScreeningRunnerScreen extends StatefulWidget {
  final String instrument;
  final int version;
  final ScreeningForm? debugForm; // Para tests sin Firestore

  const ScreeningRunnerScreen({
    super.key,
    required this.instrument,
    required this.version,
    this.debugForm,
  });

  @override
  State<ScreeningRunnerScreen> createState() => _ScreeningRunnerScreenState();
}

class _ResultAndPlanBridge extends StatelessWidget {
  final dynamic result;
  const _ResultAndPlanBridge({required this.result});
  @override
  Widget build(BuildContext context) {
    return ResultAndPlanScreen(result: result);
  }
}

class _ScreeningRunnerScreenState extends State<ScreeningRunnerScreen> {
  final _answers = <String, dynamic>{};
  late final ScreeningFormRepo _formRepo;
  late final ScreeningRepo _repo;

  @override
  void initState() {
    super.initState();
    _formRepo = ScreeningFormRepo(db: FirebaseFirestore.instance);
    _repo = ScreeningRepo();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.debugForm != null) {
      return _buildForm(context, widget.debugForm!);
    }
    return FutureBuilder<ScreeningForm>(
      future: _formRepo.getForm(widget.instrument, widget.version),
      builder: (context, snap) {
        if (!snap.hasData) {
          if (snap.hasError) {
            return Scaffold(
              appBar: AppBar(title: Text('${widget.instrument}')),
              body: Center(child: Text('Error: ${snap.error}')),
            );
          }
          return const Scaffold(body: Center(child: CircularProgressIndicator()));
        }
        return _buildForm(context, snap.data!);
      },
    );
  }

  Widget _buildForm(BuildContext context, ScreeningForm form) {
    return Scaffold(
      appBar: AppBar(title: Text('${form.instrument} v${form.version}')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          ...form.questions.map((q) => _buildQuestion(q)),
          const SizedBox(height: 24),
          FilledButton(
            onPressed: () async {
              final uid = FirebaseAuth.instance.currentUser?.uid;
              if (uid == null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Necesitás iniciar sesión')),
                );
                return;
              }
              final answers = _answers.entries
                  .map((e) => Answer(qId: e.key, value: e.value))
                  .toList();
              final result = await _repo.save(uid, form.instrument, form.version, answers);
              if (!mounted) return;
              // Navegación directa sin depender de GoRouter
              // ignore: use_build_context_synchronously
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (_) => _ResultAndPlanBridge(result: result),
                ),
              );
            },
            child: const Text('Enviar'),
          )
        ],
      ),
    );
  }

  Widget _buildQuestion(Question q) {
    switch (q.type) {
      case 'scale':
        final current = (_answers[q.id] as num?)?.toInt() ?? 0;
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(q.text),
            Slider(
              value: current.toDouble(),
              divisions: 10,
              min: 0,
              max: 10,
              label: '$current',
              onChanged: (v) => setState(() => _answers[q.id] = v.round()),
            )
          ],
        );
      case 'multi':
        final selected = Set<String>.from(_answers[q.id] as List<String>? ?? []);
        final opts = q.options ?? const [];
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(q.text),
            Wrap(
              spacing: 8,
              children: [
                for (final o in opts)
                  FilterChip(
                    label: Text(o),
                    selected: selected.contains(o),
                    onSelected: (s) {
                      setState(() {
                        if (s) {
                          selected.add(o);
                        } else {
                          selected.remove(o);
                        }
                        _answers[q.id] = selected.toList();
                      });
                    },
                  ),
              ],
            )
          ],
        );
      case 'single':
      default:
        final opts = q.options ?? const [];
        final current = _answers[q.id] as String?;
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(q.text),
            const SizedBox(height: 8),
            ...opts.map((o) => RadioListTile<String>(
                  title: Text(o),
                  value: o,
                  groupValue: current,
                  onChanged: (v) => setState(() => _answers[q.id] = v),
                )),
          ],
        );
    }
  }
}
