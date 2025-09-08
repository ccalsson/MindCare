import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mindcare/services/supabase_repository.dart';
import 'package:mindcare/services/supabase_auth_service.dart';

class AvailabilityScreen extends StatefulWidget {
  final String professionalId;
  final String professionalName;
  const AvailabilityScreen({super.key, required this.professionalId, required this.professionalName});

  @override
  State<AvailabilityScreen> createState() => _AvailabilityScreenState();
}

class _AvailabilityScreenState extends State<AvailabilityScreen> {
  final _repo = SupabaseRepository();
  final _auth = SupabaseAuthService();
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
                trailing: status == 'available'
                    ? FilledButton(
                        onPressed: () async {
                          // Ensure Supabase auth session
                          if (_auth.currentUser == null && mounted) {
                            final ok = await _promptLogin(context);
                            if (!ok) return;
                          }
                          final user = _auth.currentUser;
                          if (user == null) return;
                          try {
                            await _repo.bookSlot(
                              userId: user.id,
                              professionalId: widget.professionalId,
                              slotId: s['id'] as String,
                            );
                            if (!mounted) return;
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Turno reservado')),
                            );
                            setState(() {
                              _future = _repo.availabilityFor(widget.professionalId);
                            });
                          } catch (e) {
                            if (!mounted) return;
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(content: Text('Error al reservar: $e')),
                            );
                          }
                        },
                        child: const Text('Reservar'),
                      )
                    : Text(status),
              );
            },
          );
        },
      ),
    );
  }

  Future<bool> _promptLogin(BuildContext context) async {
    final email = TextEditingController();
    final pass = TextEditingController();
    final form = GlobalKey<FormState>();
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Ingresar a Supabase'),
        content: Form(
          key: form,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: email,
                decoration: const InputDecoration(labelText: 'Email'),
                validator: (v) => (v == null || !v.contains('@')) ? 'Email inválido' : null,
              ),
              TextFormField(
                controller: pass,
                obscureText: true,
                decoration: const InputDecoration(labelText: 'Contraseña'),
                validator: (v) => (v == null || v.length < 6) ? 'Mínimo 6 caracteres' : null,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Cancelar')),
          FilledButton(
            onPressed: () async {
              if (!form.currentState!.validate()) return;
              try {
                await _auth.signInWithPassword(email: email.text.trim(), password: pass.text);
                if (context.mounted) Navigator.pop(ctx, true);
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Error de autenticación: $e')),
                );
              }
            },
            child: const Text('Continuar'),
          ),
        ],
      ),
    );
    return ok == true;
  }
}
