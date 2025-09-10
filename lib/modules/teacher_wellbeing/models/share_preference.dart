class SharePreference {
  final String id;
  final String userId;
  final bool shareWithPrincipal;
  final bool shareWithCounselor;
  final bool shareTrendsOnly; // solo agregados, no notas

  const SharePreference({
    required this.id,
    required this.userId,
    required this.shareWithPrincipal,
    required this.shareWithCounselor,
    required this.shareTrendsOnly,
  });

  Map<String, dynamic> toJson() => {
        'userId': userId,
        'shareWithPrincipal': shareWithPrincipal,
        'shareWithCounselor': shareWithCounselor,
        'shareTrendsOnly': shareTrendsOnly,
      };

  factory SharePreference.fromJson(Map<String, dynamic> j, String id) {
    return SharePreference(
      id: id,
      userId: j['userId'] as String,
      shareWithPrincipal: (j['shareWithPrincipal'] as bool?) ?? false,
      shareWithCounselor: (j['shareWithCounselor'] as bool?) ?? false,
      shareTrendsOnly: (j['shareTrendsOnly'] as bool?) ?? true,
    );
  }
}

