class PlanItem {
  final String id;
  final String title;
  final String type; // e.g., exercise|session|habit
  final String cadence; // e.g., daily|weekly

  const PlanItem({
    required this.id,
    required this.title,
    required this.type,
    required this.cadence,
  });

  Map<String, dynamic> toMap() => {
        'id': id,
        'title': title,
        'type': type,
        'cadence': cadence,
      };

  factory PlanItem.fromMap(Map<String, dynamic> map) {
    return PlanItem(
      id: map['id'] as String,
      title: map['title'] as String,
      type: map['type'] as String,
      cadence: map['cadence'] as String,
    );
  }
}

class Plan {
  final String status; // draft|active|completed
  final List<PlanItem> items;
  final DateTime? nextCheckinAt;

  const Plan({
    required this.status,
    required this.items,
    this.nextCheckinAt,
  });

  Map<String, dynamic> toMap() => {
        'status': status,
        'items': items.map((e) => e.toMap()).toList(growable: false),
        if (nextCheckinAt != null) 'nextCheckinAt': nextCheckinAt!.toIso8601String(),
      };

  factory Plan.fromMap(Map<String, dynamic> map) {
    return Plan(
      status: map['status'] as String,
      items: (map['items'] as List)
          .map((e) => PlanItem.fromMap(Map<String, dynamic>.from(e)))
          .toList(),
      nextCheckinAt: (map['nextCheckinAt'] != null)
          ? DateTime.parse(map['nextCheckinAt'] as String)
          : null,
    );
  }
}

