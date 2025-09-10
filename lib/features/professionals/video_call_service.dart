import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';

class VideoCallService {
  final _db = FirebaseFirestore.instance;
  final _auth = FirebaseAuth.instance;

  Future<String> buildRoomName(String appointmentId, String proId) async {
    return 'mindcare_${appointmentId}_$proId'.toLowerCase();
  }

  Future<void> markCallStarted({
    required String appointmentId,
    required String room,
  }) async {
    await _db.collection('calls').add({
      'appointmentId': appointmentId,
      'room': room,
      'createdAt': FieldValue.serverTimestamp(),
      'startedAt': FieldValue.serverTimestamp(),
    });
    await _db.collection('appointments').doc(appointmentId).update({
      'status': 'in_progress',
    });
  }

  Future<void> markCallEnded({
    required String appointmentId,
    required String reason,
  }) async {
    final calls = await _db
        .collection('calls')
        .where('appointmentId', isEqualTo: appointmentId)
        .orderBy('createdAt', descending: true)
        .limit(1)
        .get();
    if (calls.docs.isNotEmpty) {
      await calls.docs.first.reference.update({
        'endedAt': FieldValue.serverTimestamp(),
        'endedReason': reason,
      });
    }
    await _db.collection('appointments').doc(appointmentId).update({
      'status': 'done',
    });
  }

  String? get userDisplayName => _auth.currentUser?.displayName ?? 'Usuario MindCare';
  String? get userEmail => _auth.currentUser?.email;
}

