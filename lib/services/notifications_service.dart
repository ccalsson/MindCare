import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

class NotificationsService {
  final FirebaseMessaging _fm;
  final FirebaseFirestore _db;
  NotificationsService({FirebaseMessaging? fm, FirebaseFirestore? db})
      : _fm = fm ?? FirebaseMessaging.instance,
        _db = db ?? FirebaseFirestore.instance;

  Future<void> registerUserToken(String uid) async {
    final token = await _fm.getToken();
    if (token == null) return;
    await _db.collection('users').doc(uid).collection('devices').doc(token).set({
      'token': token,
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  Future<void> subscribeRoom(String roomId) async {
    await _fm.subscribeToTopic('room_$roomId');
  }

  Future<void> unsubscribeRoom(String roomId) async {
    await _fm.unsubscribeFromTopic('room_$roomId');
  }
}

