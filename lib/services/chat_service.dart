import 'dart:io';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/message.dart';
import '../models/room.dart';
import 'storage_service.dart';
import 'moderation_service.dart';

class ChatService {
  final FirebaseFirestore _db;
  final FirebaseAuth _auth;
  final StorageService _storage;
  final ModerationService _moderation;
  ChatService({
    FirebaseFirestore? db,
    FirebaseAuth? auth,
    StorageService? storage,
    ModerationService? moderation,
  })  : _db = db ?? FirebaseFirestore.instance,
        _auth = auth ?? FirebaseAuth.instance,
        _storage = storage ?? StorageService(),
        _moderation = moderation ?? ModerationService();

  Stream<List<Room>> watchVisibleRooms(List<String> userRoles) {
    final q = _db.collection('rooms').orderBy('lastAt', descending: true);
    return q.snapshots().map((s) => s.docs
        .map((d) => Room.fromJson(d.data(), d.id))
        .where((r) => r.isPublic || r.allowedRoles.isEmpty || r.allowedRoles.any(userRoles.contains))
        .toList());
  }

  Stream<List<Message>> watchMessages(String roomId, {DocumentSnapshot? startAfter}) {
    Query q = _db
        .collection('rooms')
        .doc(roomId)
        .collection('messages')
        .orderBy('createdAt', descending: true)
        .limit(30);
    if (startAfter != null) q = (q as Query<Map<String, dynamic>>).startAfterDocument(startAfter);
    return q.snapshots().map((s) => s.docs.map((d) => Message.fromJson(d.data() as Map<String, dynamic>, d.id)).toList());
  }

  Future<void> sendText(String roomId, String text, {List<String> mentions = const []}) async {
    final uid = _auth.currentUser?.uid;
    if (uid == null) throw StateError('No auth');
    final needsReview = _moderation.shouldFlag(text);
    final msg = {
      'senderId': uid,
      'text': text,
      'type': 'text',
      'createdAt': FieldValue.serverTimestamp(),
      'editedAt': null,
      'deleted': false,
      'mentions': mentions,
      'attachments': [],
      'needsReview': needsReview,
    };
    final ref = _db.collection('rooms').doc(roomId).collection('messages');
    await ref.add(msg);
    await _db.collection('rooms').doc(roomId).set({
      'lastMessage': needsReview ? 'Mensaje enviado' : text,
      'lastAt': FieldValue.serverTimestamp(),
    }, SetOptions(merge: true));
  }

  Future<void> sendAttachment(String roomId, File file, {String? mime}) async {
    if (file.lengthSync() > 10 * 1024 * 1024) {
      throw StateError('Archivo demasiado grande (>10MB)');
    }
    final uid = _auth.currentUser?.uid;
    if (uid == null) throw StateError('No auth');
    final name = file.path.split('/').last;
    final type = (mime?.startsWith('image/') ?? false)
        ? 'image'
        : (mime?.startsWith('audio/') ?? false)
            ? 'voice'
            : 'file';
    final (url, path) = await _storage.uploadFile(roomId, file, fileName: name, mime: mime ?? 'application/octet-stream');
    final msg = {
      'senderId': uid,
      'text': '',
      'type': type,
      'createdAt': FieldValue.serverTimestamp(),
      'editedAt': null,
      'deleted': false,
      'mentions': [],
      'attachments': [
        {
          'url': url,
          'storagePath': path,
          'mime': mime ?? 'application/octet-stream',
          'size': file.lengthSync(),
        }
      ],
      'needsReview': false,
    };
    final ref = _db.collection('rooms').doc(roomId).collection('messages');
    await ref.add(msg);
    await _db.collection('rooms').doc(roomId).set({
      'lastMessage': '[adjunto]',
      'lastAt': FieldValue.serverTimestamp(),
    }, SetOptions(merge: true));
  }
}

