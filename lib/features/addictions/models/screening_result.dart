class ScreeningResult {
  final String instrument;
  final int version;
  final int score;
  final String riskBand; // low | moderate | high
  final DateTime createdAt;

  const ScreeningResult({
    required this.instrument,
    required this.version,
    required this.score,
    required this.riskBand,
    required this.createdAt,
  });

  Map<String, dynamic> toMap() => {
        'instrument': instrument,
        'version': version,
        'score': score,
        'riskBand': riskBand,
        'createdAt': createdAt.toIso8601String(),
      };

  factory ScreeningResult.fromMap(Map<String, dynamic> map) {
    return ScreeningResult(
      instrument: map['instrument'] as String,
      version: (map['version'] as num).toInt(),
      score: (map['score'] as num).toInt(),
      riskBand: map['riskBand'] as String,
      createdAt: DateTime.parse(map['createdAt'] as String),
    );
  }
}

