import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../state/teacher_wellbeing_controller.dart';

class CheckinPage extends StatefulWidget {
  const CheckinPage({super.key});

  @override
  State<CheckinPage> createState() => _CheckinPageState();
}

class _CheckinPageState extends State<CheckinPage> {
  int _mood = 3;
  int _energy = 3;
  final _tags = <String>{};
  final _noteCtrl = TextEditingController();
  final _tagOptions = const ['estrés', 'sueño', 'aula', 'familia', 'administrativos', 'salud'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Check-in rápido')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Semantics(
            label: 'Ánimo $_mood de 5',
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Ánimo'),
                Slider(
                  value: _mood.toDouble(),
                  min: 1,
                  max: 5,
                  divisions: 4,
                  label: '$_mood',
                  onChanged: (v) => setState(() => _mood = v.round()),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          Semantics(
            label: 'Energía $_energy de 5',
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Energía'),
                Slider(
                  value: _energy.toDouble(),
                  min: 1,
                  max: 5,
                  divisions: 4,
                  label: '$_energy',
                  onChanged: (v) => setState(() => _energy = v.round()),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          const Text('Tags'),
          Wrap(
            spacing: 8,
            children: _tagOptions
                .map((t) => FilterChip(
                      label: Text(t),
                      selected: _tags.contains(t),
                      onSelected: (s) => setState(() => s ? _tags.add(t) : _tags.remove(t)),
                    ))
                .toList(),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _noteCtrl,
            decoration: const InputDecoration(
              labelText: 'Nota (opcional)',
              counterText: '',
            ),
            maxLength: 200,
            maxLines: 4,
          ),
          const SizedBox(height: 16),
          FilledButton(
            onPressed: _save,
            child: const Text('Guardar check-in'),
          )
        ],
      ),
    );
  }

  Future<void> _save() async {
    if (FirebaseAuth.instance.currentUser == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Iniciá sesión')));
      return;
    }
    try {
      await context.read<TeacherWellbeingController>().doCheckin(
            mood: _mood,
            energy: _energy,
            tags: _tags.toList(),
            note: _noteCtrl.text.isEmpty ? null : _noteCtrl.text,
          );
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Check-in guardado')));
      Navigator.pop(context);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
    }
  }
}

