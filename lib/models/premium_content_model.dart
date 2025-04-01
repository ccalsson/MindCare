class PremiumContent {
  final String id;
  final String title;
  final String description;
  final ContentType type;
  final List<String> tags;
  final Map<String, dynamic> metadata;
  final DateTime releaseDate;

  PremiumContent({
    required this.id,
    required this.title,
    required this.description,
    required this.type,
    required this.tags,
    required this.metadata,
    required this.releaseDate,
  });
}

enum ContentType {
  guidedMeditation,
  masterclass,
  workshop,
  course,
  expertTalk,
}

class PremiumContentService {
  final _firestore = FirebaseFirestore.instance;

  Future<void> addPremiumContent(PremiumContent content) async {
    await _firestore.collection('premium_content').add({
      'title': content.title,
      'description': content.description,
      'type': content.type.toString(),
      'tags': content.tags,
      'metadata': content.metadata,
      'releaseDate': content.releaseDate.toIso8601String(),
    });
  }

  Stream<List<PremiumContent>> getPremiumContent() {
    return _firestore
        .collection('premium_content')
        .orderBy('releaseDate', descending: true)
        .snapshots()
        .map((snapshot) => snapshot.docs
            .map((doc) => PremiumContent.fromMap(doc.data()))
            .toList());
  }
} 