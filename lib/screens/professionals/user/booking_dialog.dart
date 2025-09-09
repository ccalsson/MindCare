import 'package:flutter/material.dart';
import '../../../services/availability_service.dart';
import '../../../services/appointment_service.dart';
import '../../../services/stripe_service.dart';

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
  List<DateTime> _exceptions = const [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final slots = await _availability.getWeekly(widget.proId);
    _exceptions = await _availability.getExceptions(widget.proId);
    final now = DateTime.now();
    final List<DateTime> opts = [];
    // Generate options for next 14 days based on weekly slots
    for (int day = 0; day < 14; day++) {
      final d = DateTime(now.year, now.month, now.day).add(Duration(days: day));
      final dow = d.weekday; // 1=Mon..7=Sun
      final isException = _exceptions.any((e) => e.year == d.year && e.month == d.month && e.day == d.day);
      if (isException) continue;
      for (final s in slots.where((s) => s.dayOfWeek == dow)) {
        final partsStart = s.startTime.split(':');
        final partsEnd = s.endTime.split(':');
        if (partsStart.length == 2 && partsEnd.length == 2) {
          final start = DateTime(d.year, d.month, d.day, int.parse(partsStart[0]), int.parse(partsStart[1]));
          final end = DateTime(d.year, d.month, d.day, int.parse(partsEnd[0]), int.parse(partsEnd[1]));
          // break in 50-min slots
          var cur = start;
          while (cur.isBefore(end)) {
            if (cur.isAfter(now)) opts.add(cur);
            cur = cur.add(const Duration(minutes: 50));
          }
        }
      }
    }
    setState(() {
      _options = opts.take(24).toList();
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
                              final ok = await StripeService.maybePay(amountCents: 10000, currency: 'usd', requirePayment: false);
                              if (!ok) return;
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
