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

  factory Appointment.fromJson(Map<String, dynamic> json) {
    return Appointment(
      id: (json['id'] ?? '').toString(),
      proId: (json['proId'] ?? '').toString(),
      userId: (json['userId'] ?? '').toString(),
      startsAt: DateTime.tryParse(json['startsAt']?.toString() ?? '') ??
          (json['startsAt'] is int ? DateTime.fromMillisecondsSinceEpoch(json['startsAt'] as int) : DateTime.now()),
      endsAt: DateTime.tryParse(json['endsAt']?.toString() ?? '') ??
          (json['endsAt'] is int ? DateTime.fromMillisecondsSinceEpoch(json['endsAt'] as int) : DateTime.now()),
      status: (json['status'] ?? '').toString(),
      notes: json['notes']?.toString(),
      paymentId: json['paymentId']?.toString(),
    );
  }
}
