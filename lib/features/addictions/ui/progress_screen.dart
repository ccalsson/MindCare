import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

class ProgressScreen extends StatelessWidget {
  const ProgressScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) {
      return const Scaffold(body: Center(child: Text('Iniciá sesión')));
    }
    final col = FirebaseFirestore.instance
        .collection('users')
        .doc(uid)
        .collection('journals')
        .orderBy('createdAt', descending: true)
        .limit(7);
    return Scaffold(
      appBar: AppBar(title: const Text('Progreso semanal')),
      body: StreamBuilder(
        stream: col.snapshots(),
        builder: (context, snap) {
          if (!snap.hasData) {
            return const Center(child: CircularProgressIndicator());
          }
          final docs = (snap.data as QuerySnapshot).docs;
          final items = docs
              .map((d) => d.data() as Map<String, dynamic>)
              .map((m) => (
                    mood: (m['mood'] as num?)?.toInt() ?? 0,
                    craving: (m['craving'] as num?)?.toInt() ?? 0,
                    createdAt: DateTime.tryParse(m['createdAt']?.toString() ?? '') ?? DateTime.now(),
                  ))
              .toList()
              .reversed
              .toList();
          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text('Craving (0..10)'),
                const SizedBox(height: 8),
                _BarChart(values: items.map((e) => e.craving.toDouble()).toList(), max: 10),
                const SizedBox(height: 16),
                const Text('Ánimo (-5..5)'),
                const SizedBox(height: 8),
                _BarChart(values: items.map((e) => (e.mood + 5).toDouble()).toList(), max: 10),
              ],
            ),
          );
        },
      ),
    );
  }
}

class _BarChart extends StatelessWidget {
  final List<double> values;
  final double max;
  const _BarChart({required this.values, required this.max});

  @override
  Widget build(BuildContext context) {
    if (values.isEmpty) return const SizedBox.shrink();
    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: values
          .map((v) => Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4),
                  child: Container(
                    height: (v / max) * 120.0,
                    color: Colors.blue.shade300,
                  ),
                ),
              ))
          .toList(),
    );
  }
}

