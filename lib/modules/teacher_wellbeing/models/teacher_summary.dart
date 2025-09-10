class TeacherSummary {
  final String id; // e.g. 2025-W37 or 2025-09
  final String userId;
  final String period; // week | month
  final double moodAvg;
  final double energyAvg;
  final int checkinsCount;
  final List<String> topTags;

  const TeacherSummary({
    required this.id,
    required this.userId,
    required this.period,
    required this.moodAvg,
    required this.energyAvg,
    required this.checkinsCount,
    required this.topTags,
  });

  Map<String, dynamic> toJson() => {
        'userId': userId,
        'period': period,
        'moodAvg': moodAvg,
        'energyAvg': energyAvg,
        'checkinsCount': checkinsCount,
        'topTags': topTags,
      };

  factory TeacherSummary.fromJson(Map<String, dynamic> j, String id) {
    return TeacherSummary(
      id: id,
      userId: j['userId'] as String,
      period: j['period'] as String,
      moodAvg: (j['moodAvg'] as num).toDouble(),
      energyAvg: (j['energyAvg'] as num).toDouble(),
      checkinsCount: (j['checkinsCount'] as num).toInt(),
      topTags: (j['topTags'] as List).map((e) => e.toString()).toList(),
    );
  }
}

