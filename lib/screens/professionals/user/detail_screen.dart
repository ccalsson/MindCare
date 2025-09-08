import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../../services/professional_service.dart';
import '../../../models/professional.dart';
import 'booking_dialog.dart';

class ProfessionalDetailScreen extends StatefulWidget {
  final String proId;
  const ProfessionalDetailScreen({super.key, required this.proId});

  @override
  State<ProfessionalDetailScreen> createState() => _ProfessionalDetailScreenState();
}

class _ProfessionalDetailScreenState extends State<ProfessionalDetailScreen> {
  final _service = ProfessionalService();
  Professional? _pro;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final p = await _service.getById(widget.proId);
    setState(() {
      _pro = p;
      _loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Perfil del Profesional')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _pro == null
              ? const Center(child: Text('No encontrado'))
              : Padding(
                  padding: const EdgeInsets.all(16),
                  child: ListView(
                    children: [
                      Row(
                        children: [
                          CircleAvatar(radius: 36, child: Text(_initials(_pro!.fullName))),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(_pro!.fullName, style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.w600)),
                                const SizedBox(height: 6),
                                Text(_pro!.specialties.join(' · ')),
                              ],
                            ),
                          )
                        ],
                      ),
                      const SizedBox(height: 16),
                      Text(_pro!.bio),
                      const SizedBox(height: 16),
                      Row(
                        children: [
                          FilledButton(
                            onPressed: () async {
                              await showModalBottomSheet(
                                context: context,
                                isScrollControlled: true,
                                builder: (_) => BookingDialog(proId: _pro!.proId),
                              );
                            },
                            child: const Text('Agendar cita'),
                          ),
                          const SizedBox(width: 12),
                          OutlinedButton(onPressed: () => Navigator.pop(context), child: const Text('Volver')),
                        ],
                      )
                    ],
                  ),
                ),
    );
  }

  String _initials(String name) {
    final parts = name.trim().split(' ');
    return parts.take(2).map((e) => e.isNotEmpty ? e[0] : '').join().toUpperCase();
  }
}

