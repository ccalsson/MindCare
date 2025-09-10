import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import '../models/share_preference.dart';
import '../services/share_prefs_service.dart';

class SharePreferencesPage extends StatefulWidget {
  const SharePreferencesPage({super.key});

  @override
  State<SharePreferencesPage> createState() => _SharePreferencesPageState();
}

class _SharePreferencesPageState extends State<SharePreferencesPage> {
  bool principal = false;
  bool counselor = false;
  bool trendsOnly = true;
  final _service = SharePrefsService();

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;
    final p = await _service.get(uid, 'default');
    if (mounted && p != null) {
      setState(() {
        principal = p.shareWithPrincipal;
        counselor = p.shareWithCounselor;
        trendsOnly = p.shareTrendsOnly;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Compartir (opcional)')),
      body: ListView(
        children: [
          SwitchListTile(
            title: const Text('Compartir con directivo'),
            value: principal,
            onChanged: (v) => setState(() => principal = v),
          ),
          SwitchListTile(
            title: const Text('Compartir con orientación'),
            value: counselor,
            onChanged: (v) => setState(() => counselor = v),
          ),
          SwitchListTile(
            title: const Text('Solo tendencias (no notas)'),
            value: trendsOnly,
            onChanged: (v) => setState(() => trendsOnly = v),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: FilledButton(
              onPressed: _save,
              child: const Text('Guardar preferencias'),
            ),
          ),
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text('Por diseño, tus check-ins y notas son privados a menos que elijas compartir.'),
          )
        ],
      ),
    );
  }

  Future<void> _save() async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;
    final p = SharePreference(
      id: 'default',
      userId: uid,
      shareWithPrincipal: principal,
      shareWithCounselor: counselor,
      shareTrendsOnly: trendsOnly,
    );
    await _service.save(p);
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Guardado')));
  }
}

