import 'package:flutter/material.dart';
import '../../../services/availability_service.dart';
import '../../../services/appointment_service.dart';

class BookingDialog extends StatefulWidget {
  final String proId;
  const BookingDialog({super.key, required this.proId});

  @override
  State<BookingDialog> createState() => _BookingDialogState();
}

class _BookingDialogState extends State<BookingDialog> {
  final _availability = AvailabilityService();
  final _appointments = AppointmentService();
  DateTime? _selected;
  bool _loading = true;
  List<DateTime> _options = const [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    // TODO: Replace with real weekly slots -> expand to concrete DateTimes
    final now = DateTime.now();
    setState(() {
      _options = [
        DateTime(now.year, now.month, now.day + 1, 10),
        DateTime(now.year, now.month, now.day + 1, 12),
        DateTime(now.year, now.month, now.day + 2, 14),
      ];
      _loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: SafeArea(
        child: AnimatedPadding(
          duration: const Duration(milliseconds: 150),
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Text('Elegí fecha y hora', style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16)),
              const SizedBox(height: 12),
              if (_loading) const LinearProgressIndicator(),
              if (!_loading)
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: _options
                      .map((d) => ChoiceChip(
                            label: Text('${d.day}/${d.month} ${d.hour}:00'),
                            selected: _selected == d,
                            onSelected: (_) => setState(() => _selected = d),
                          ))
                      .toList(),
                ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () => Navigator.pop(context),
                      child: const Text('Cancelar'),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: FilledButton(
                      onPressed: _selected == null
                          ? null
                          : () async {
                              final start = _selected!;
                              final end = start.add(const Duration(minutes: 50));
                              // TODO: Stripe payment before create when required
                              await _appointments.create(widget.proId, 'me', start, end);
                              if (!mounted) return;
                              Navigator.pop(context);
                              ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Cita agendada')));
                            },
                      child: const Text('Confirmar'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

