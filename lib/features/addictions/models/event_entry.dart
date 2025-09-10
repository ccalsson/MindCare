class EventEntry {
  final String type; // checkin|relapse|milestone|session
  final Map<String, dynamic> payload;
  final DateTime createdAt;

  const EventEntry({
    required this.type,
    required this.payload,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'type': type,
        'payload': payload,
        'createdAt': createdAt.toIso8601String(),
      };

  factory EventEntry.fromMap(Map<String, dynamic> map) {
    return EventEntry(
      type: map['type'] as String,
      payload: Map<String, dynamic>.from(map['payload'] as Map? ?? {}),
      createdAt: DateTime.parse(map['createdAt'] as String),
    );
  }
}

