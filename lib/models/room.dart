class Room {
  final String id;
  final String name;
  final String kind; // general|students|teachers|professionals|community
  final bool isPublic;
  final List<String> allowedRoles;
  final DateTime? createdAt;
  final String createdBy;
  final int membersCount;
  final String? lastMessage;
  final DateTime? lastAt;

  const Room({
    required this.id,
    required this.name,
    required this.kind,
    required this.isPublic,
    required this.allowedRoles,
    required this.createdBy,
    this.createdAt,
    this.membersCount = 0,
    this.lastMessage,
    this.lastAt,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        'kind': kind,
        'isPublic': isPublic,
        'allowedRoles': allowedRoles,
        'createdAt': createdAt?.toIso8601String(),
        'createdBy': createdBy,
        'membersCount': membersCount,
        if (lastMessage != null) 'lastMessage': lastMessage,
        if (lastAt != null) 'lastAt': lastAt!.toIso8601String(),
      };

  factory Room.fromJson(Map<String, dynamic> j, String id) {
    return Room(
      id: id,
      name: j['name'] as String,
      kind: j['kind'] as String,
      isPublic: (j['isPublic'] as bool?) ?? false,
      allowedRoles: (j['allowedRoles'] as List?)?.map((e) => e.toString()).toList() ?? const [],
      createdBy: j['createdBy'] as String? ?? '',
      createdAt: (j['createdAt'] != null) ? DateTime.tryParse(j['createdAt'].toString()) : null,
      membersCount: (j['membersCount'] as num?)?.toInt() ?? 0,
      lastMessage: j['lastMessage'] as String?,
      lastAt: (j['lastAt'] != null) ? DateTime.tryParse(j['lastAt'].toString()) : null,
    );
  }
}

