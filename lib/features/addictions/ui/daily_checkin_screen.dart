import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../data/journal_repo.dart';
import '../models/journal_entry.dart';

class DailyCheckinScreen extends StatefulWidget {
  const DailyCheckinScreen({super.key});

  @override
  State<DailyCheckinScreen> createState() => _DailyCheckinScreenState();
}

class _DailyCheckinScreenState extends State<DailyCheckinScreen> {
  final _repo = JournalRepo();
  int _mood = 0;
  int _craving = 0;
  final _triggers = <String>{};
  final _noteCtrl = TextEditingController();
  final List<String> _triggerOptions = const ['Estrés', 'Soledad', 'Fiesta', 'Rutina', 'Ansiedad'];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Check-in diario')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text('Ánimo (mood): $_mood'),
          Slider(
            value: (_mood + 5).toDouble(),
            min: 0,
            max: 10,
            divisions: 10,
            label: '$_mood',
            onChanged: (v) => setState(() => _mood = v.round() - 5),
          ),
          const SizedBox(height: 8),
          Text('Deseo/ansia (craving): $_craving'),
          Slider(
            value: _craving.toDouble(),
            min: 0,
            max: 10,
            divisions: 10,
            label: '$_craving',
            onChanged: (v) => setState(() => _craving = v.round()),
          ),
          const SizedBox(height: 8),
          const Text('Disparadores (triggers):'),
          Wrap(
            spacing: 8,
            children: _triggerOptions
                .map((t) => FilterChip(
                      label: Text(t),
                      selected: _triggers.contains(t),
                      onSelected: (s) => setState(() => s ? _triggers.add(t) : _triggers.remove(t)),
                    ))
                .toList(),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _noteCtrl,
            decoration: const InputDecoration(labelText: 'Nota (opcional)'),
            maxLines: 3,
          ),
          const SizedBox(height: 16),
          FilledButton(
            onPressed: _onSave,
            child: const Text('Guardar'),
          ),
          if (_craving >= 8) ...[
            const SizedBox(height: 16),
            FilledButton.tonal(
              onPressed: () => context.push('/addictions/help'),
              child: const Text('Necesito ayuda ahora'),
            ),
          ],
        ],
      ),
    );
  }

  Future<void> _onSave() async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Iniciá sesión')));
      return;
    }
    final e = JournalEntry(
      mood: _mood,
      craving: _craving,
      triggers: _triggers.toList(),
      note: _noteCtrl.text.isEmpty ? null : _noteCtrl.text,
      createdAt: DateTime.now(),
    );
    await _repo.addEntry(uid, e);
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Check-in guardado')));
    if (_craving >= 8) {
      // Mostrar HelpNowSheet sugiriendo ayuda inmediata
      // ignore: use_build_context_synchronously
      context.push('/addictions/help');
    }
  }
}
