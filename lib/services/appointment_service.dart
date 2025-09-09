import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/appointment.dart';

class AppointmentService {
  final _db = FirebaseFirestore.instance;
  Future<Appointment> create({
    required String proId,
    required String userId,
    required DateTime startsAt,
    required DateTime endsAt,
  }) async {
    // prevent double booking (simple overlap check)
    final overlap = await _db
        .collection('appointments')
        .where('proId', isEqualTo: proId)
        .where('startsAt', isLessThan: Timestamp.fromDate(endsAt))
        .where('endsAt', isGreaterThan: Timestamp.fromDate(startsAt))
        .limit(1)
        .get();
    if (overlap.docs.isNotEmpty) {
      throw StateError('Ya existe una cita en ese horario');
    }

    final ref = await _db.collection('appointments').add({
      'proId': proId,
      'userId': userId,
      'startsAt': Timestamp.fromDate(startsAt),
      'endsAt': Timestamp.fromDate(endsAt),
      'status': 'confirmed',
      'createdAt': FieldValue.serverTimestamp(),
    });
    return Appointment(
      id: ref.id,
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
