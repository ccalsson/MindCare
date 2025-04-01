enum ResourceType {
  meditation,
  relaxingSound,
  music,
  guidedExercise
}

class AudioResource {
  final String id;
  final String title;
  final String description;
  final String audioUrl;
  final ResourceType type;
  final bool isPremium;
  final int duration; // en segundos
  final String? thumbnailUrl;
  final Map<String, dynamic>? metadata;

  AudioResource({
    required this.id,
    required this.title,
    required this.description,
    required this.audioUrl,
    required this.type,
    required this.isPremium,
    required this.duration,
    this.thumbnailUrl,
    this.metadata,
  });

  factory AudioResource.fromMap(Map<String, dynamic> map) {
    return AudioResource(
      id: map['id'],
      title: map['title'],
      description: map['description'],
      audioUrl: map['audioUrl'],
      type: ResourceType.values.firstWhere(
        (e) => e.toString() == 'ResourceType.${map["type"]}'
      ),
      isPremium: map['isPremium'] ?? false,
      duration: map['duration'],
      thumbnailUrl: map['thumbnailUrl'],
      metadata: map['metadata'],
    );
  }
} 