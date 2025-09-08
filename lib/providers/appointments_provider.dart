import 'package:flutter/foundation.dart';
import '../models/appointment.dart';
import '../services/appointment_service.dart';

class AppointmentsProvider extends ChangeNotifier {
  final _service = AppointmentService();
  final List<Appointment> _appointments = [];

  List<Appointment> get appointments => List.unmodifiable(_appointments);

  Future<void> create(String proId, String userId, DateTime start, DateTime end) async {
    final a = await _service.create(proId: proId, userId: userId, startsAt: start, endsAt: end);
    _appointments.insert(0, a);
    notifyListeners();
  }
}

