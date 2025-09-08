import '../models/appointment.dart';

class AppointmentService {
  Future<Appointment> create({
    required String proId,
    required String userId,
    required DateTime startsAt,
    required DateTime endsAt,
  }) async {
    // TODO: Firestore + Stripe payment intent
    return Appointment(
      id: 'temp',
      proId: proId,
      userId: userId,
      startsAt: startsAt,
      endsAt: endsAt,
      status: 'confirmed',
    );
  }

  Future<void> confirm(String appointmentId) async {}
  Future<void> cancel(String appointmentId) async {}
  Future<void> reschedule(String appointmentId, DateTime newStart, DateTime newEnd) async {}
}

