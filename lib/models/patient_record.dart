class PatientRecordEntry {
  final DateTime date;
  final String note;
  final List<String> tags;
  final List<String> attachments; // URLs

  PatientRecordEntry({
    required this.date,
    required this.note,
    this.tags = const [],
    this.attachments = const [],
  });
}

class PatientRecord {
  final String id;
  final String proId;
  final String userId;
  final List<PatientRecordEntry> entries;

  PatientRecord({
    required this.id,
    required this.proId,
    required this.userId,
    this.entries = const [],
  });
}

