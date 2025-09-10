class JournalEntry {
  final int mood; // -5..5
  final int craving; // 0..10
  final List<String> triggers;
  final String? note;
  final DateTime createdAt;

  const JournalEntry({
    required this.mood,
    required this.craving,
    required this.triggers,
    this.note,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'mood': mood,
        'craving': craving,
        'triggers': triggers,
        if (note != null) 'note': note,
        'createdAt': createdAt.toIso8601String(),
      };

  factory JournalEntry.fromMap(Map<String, dynamic> map) {
    return JournalEntry(
      mood: (map['mood'] as num).toInt(),
      craving: (map['craving'] as num).toInt(),
      triggers: (map['triggers'] as List).map((e) => e.toString()).toList(),
      note: map['note'] as String?,
      createdAt: DateTime.parse(map['createdAt'] as String),
    );
  }
}

