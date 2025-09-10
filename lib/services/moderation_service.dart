class ModerationService {
  static const List<String> _blocked = [
    'palabra_bloqueada',
    'ofensa1',
    'ofensa2',
  ];

  bool shouldFlag(String text) {
    final lower = text.toLowerCase();
    for (final w in _blocked) {
      if (lower.contains(w)) return true;
    }
    return false;
  }
}

