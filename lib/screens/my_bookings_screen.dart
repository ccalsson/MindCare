import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mindcare/services/supabase_repository.dart';
import 'package:mindcare/services/supabase_auth_service.dart';

class MyBookingsScreen extends StatefulWidget {
  const MyBookingsScreen({super.key});

  @override
  State<MyBookingsScreen> createState() => _MyBookingsScreenState();
}

class _MyBookingsScreenState extends State<MyBookingsScreen> {
  final _repo = SupabaseRepository();
  final _auth = SupabaseAuthService();
  late Future<List<Map<String, dynamic>>> _future;
  final _fmt = DateFormat('EEE d MMM HH:mm');
  String? _error;

  @override
  void initState() {
    super.initState();
    _future = _load();
  }

  Future<List<Map<String, dynamic>>> _load() async {
    if (_auth.currentUser == null) {
      setState(() => _error = 'Ingresá primero para ver tus reservas');
      return [];
    }
    try {
      return await _repo.myBookings();
    } catch (e) {
      setState(() => _error = e.toString());
      return [];
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mis reservas')),
      body: _error != null
          ? Center(child: Text(_error!))
          : FutureBuilder<List<Map<String, dynamic>>>(
              future: _future,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasError) {
                  return Center(child: Text('Error: ${snapshot.error}'));
                }
                final items = snapshot.data ?? const [];
                if (items.isEmpty) {
                  return const Center(child: Text('No tenés reservas aún'));
                }
                return ListView.separated(
                  itemCount: items.length,
                  separatorBuilder: (_, __) => const Divider(height: 1),
                  itemBuilder: (context, i) {
                    final b = items[i];
                    final pro = b['professionals'] as Map<String, dynamic>?;
                    final slot = b['availability_slots'] as Map<String, dynamic>?;
                    final title = pro != null ? pro['name'] as String? ?? 'Profesional' : 'Profesional';
                    final when = slot != null && slot['start_at'] is String
                        ? _fmt.format(DateTime.parse(slot['start_at'] as String).toLocal())
                        : '';
                    final price = '${b['price_amount'] ?? 0} ${b['price_currency'] ?? ''}'.trim();
                    final status = (b['status'] ?? '').toString();
                    return ListTile(
                      title: Text(title),
                      subtitle: Text(when.isNotEmpty ? '$when · $status' : status),
                      trailing: Text(price),
                    );
                  },
                );
              },
            ),
    );
  }
}

