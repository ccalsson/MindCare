class Professional {
  final String proId;
  final String userId;
  final String fullName;
  final List<String> specialties;
  final String bio;
  final String? avatarUrl;
  final String? phone;
  final double? price;
  final bool isActive;
  final double? rating;
  final List<String> locations;
  final List<String> languages;

  Professional({
    required this.proId,
    required this.userId,
    required this.fullName,
    required this.specialties,
    required this.bio,
    this.avatarUrl,
    this.phone,
    this.price,
    this.isActive = true,
    this.rating,
    this.locations = const [],
    this.languages = const [],
  });

  factory Professional.fromMap(Map<String, dynamic> m, String id) => Professional(
        proId: id,
        userId: m['userId'] ?? '',
        fullName: m['fullName'] ?? '',
        specialties: List<String>.from(m['specialties'] ?? const []),
        bio: m['bio'] ?? '',
        avatarUrl: m['avatarUrl'],
        phone: m['phone'],
        price: (m['price'] as num?)?.toDouble(),
        isActive: (m['isActive'] as bool?) ?? true,
        rating: (m['rating'] as num?)?.toDouble(),
        locations: List<String>.from(m['locations'] ?? const []),
        languages: List<String>.from(m['languages'] ?? const []),
      );
}

