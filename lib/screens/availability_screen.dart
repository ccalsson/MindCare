import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mindcare/services/supabase_repository.dart';

class AvailabilityScreen extends StatefulWidget {
  final String professionalId;
  final String professionalName;
  const AvailabilityScreen({super.key, required this.professionalId, required this.professionalName});

  @override
  State<AvailabilityScreen> createState() => _AvailabilityScreenState();
}

class _AvailabilityScreenState extends State<AvailabilityScreen> {
  final _repo = SupabaseRepository();
  late Future<List<Map<String, dynamic>>> _future;
  final _fmt = DateFormat('EEE d MMM HH:mm');

  @override
  void initState() {
    super.initState();
    _future = _repo.availabilityFor(widget.professionalId);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Disponibilidad - ${widget.professionalName}')),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: _future,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          }
          final slots = snapshot.data ?? const [];
          if (slots.isEmpty) {
            return const Center(child: Text('No hay turnos disponibles'));
          }
          return ListView.separated(
            itemCount: slots.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, i) {
              final s = slots[i];
              final start = DateTime.tryParse(s['start_at'] as String? ?? '');
              final end = DateTime.tryParse(s['end_at'] as String? ?? '');
              final when = start != null ? _fmt.format(start.toLocal()) : '—';
              final status = (s['status'] ?? 'available') as String;
              return ListTile(
                title: Text(when),
                subtitle: end != null ? Text('Hasta ${_fmt.format(end.toLocal())}') : null,
                trailing: Text(status),
              );
            },
          );
        },
      ),
    );
  }
}

