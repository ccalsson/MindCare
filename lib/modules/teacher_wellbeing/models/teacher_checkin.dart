class TeacherCheckin {
  final String id;
  final String userId;
  final DateTime createdAt;
  final int mood; // 1..5
  final int energy; // 1..5
  final List<String> tags; // ["estrés","sueño","aula","familia"]
  final String? note; // opcional

  const TeacherCheckin({
    required this.id,
    required this.userId,
    required this.createdAt,
    required this.mood,
    required this.energy,
    required this.tags,
    this.note,
  });

  Map<String, dynamic> toJson() => {
        'userId': userId,
        'createdAt': createdAt.toIso8601String(),
        'mood': mood,
        'energy': energy,
        'tags': tags,
        if (note != null) 'note': note,
      };

  factory TeacherCheckin.fromJson(Map<String, dynamic> j, String id) {
    return TeacherCheckin(
      id: id,
      userId: j['userId'] as String,
      createdAt: DateTime.parse(j['createdAt'] as String),
      mood: (j['mood'] as num).toInt(),
      energy: (j['energy'] as num).toInt(),
      tags: (j['tags'] as List).map((e) => e.toString()).toList(),
      note: j['note'] as String?,
    );
  }
}

