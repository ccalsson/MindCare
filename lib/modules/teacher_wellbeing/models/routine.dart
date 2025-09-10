class Routine {
  final String id;
  final String title;
  final int durationMin;
  final List<String> steps;
  final String? audioUrl;

  const Routine({
    required this.id,
    required this.title,
    required this.durationMin,
    required this.steps,
    this.audioUrl,
  });

  Map<String, dynamic> toJson() => {
        'title': title,
        'durationMin': durationMin,
        'steps': steps,
        if (audioUrl != null) 'audioUrl': audioUrl,
      };

  factory Routine.fromJson(Map<String, dynamic> j, String id) {
    return Routine(
      id: id,
      title: j['title'] as String,
      durationMin: (j['durationMin'] as num).toInt(),
      steps: (j['steps'] as List).map((e) => e.toString()).toList(),
      audioUrl: j['audioUrl'] as String?,
    );
  }
}

