class Appointment {
  final String id;
  final String proId;
  final String userId;
  final DateTime startsAt;
  final DateTime endsAt;
  final String status; // pending/confirmed/canceled/completed
  final String? notes;
  final String? paymentId;

  Appointment({
    required this.id,
    required this.proId,
    required this.userId,
    required this.startsAt,
    required this.endsAt,
    required this.status,
    this.notes,
    this.paymentId,
  });
}

