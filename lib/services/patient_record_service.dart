import '../models/patient_record.dart';

class PatientRecordService {
  Future<void> addNote(String proId, String userId, String note) async {
    // TODO: Persist in Firestore under patient_records
  }

  Future<List<PatientRecordEntry>> list(String proId, String userId) async {
    // TODO: Query Firestore
    return [];
  }
}

