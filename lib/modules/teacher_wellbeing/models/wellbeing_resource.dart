class WellbeingResource {
  final String id;
  final String title;
  final String kind; // articulo | audio | video
  final int durationMin; // 3..15
  final List<String> topics; // ["estres","mindfulness","organizacion"]
  final String contentUrl; // gs:// or https://
  final String description;

  const WellbeingResource({
    required this.id,
    required this.title,
    required this.kind,
    required this.durationMin,
    required this.topics,
    required this.contentUrl,
    required this.description,
  });

  Map<String, dynamic> toJson() => {
        'title': title,
        'kind': kind,
        'durationMin': durationMin,
        'topics': topics,
        'contentUrl': contentUrl,
        'description': description,
      };

  factory WellbeingResource.fromJson(Map<String, dynamic> j, String id) {
    return WellbeingResource(
      id: id,
      title: j['title'] as String,
      kind: j['kind'] as String,
      durationMin: (j['durationMin'] as num).toInt(),
      topics: (j['topics'] as List).map((e) => e.toString()).toList(),
      contentUrl: j['contentUrl'] as String,
      description: j['description'] as String,
    );
  }
}

