import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_core/firebase_core.dart';

// Seed 5 base rooms. Run with flutter or dart after Firebase init is configured.
// dart run tools/seed_rooms.dart
Future<void> main() async {
  try {
    await Firebase.initializeApp();
  } catch (_) {}
  final db = FirebaseFirestore.instance;
  final rooms = [
    { 'id': 'general', 'name': 'General', 'kind': 'general', 'isPublic': true,  'allowedRoles': [] },
    { 'id': 'students', 'name': 'Estudiantes', 'kind': 'students', 'isPublic': false, 'allowedRoles': ['student'] },
    { 'id': 'teachers', 'name': 'Docentes', 'kind': 'teachers', 'isPublic': false, 'allowedRoles': ['teacher'] },
    { 'id': 'professionals', 'name': 'Profesionales', 'kind': 'professionals', 'isPublic': false, 'allowedRoles': ['pro'] },
    { 'id': 'community', 'name': 'Comunidad', 'kind': 'community', 'isPublic': true, 'allowedRoles': ['community'] },
  ];
  for (final r in rooms) {
    await db.collection('rooms').doc(r['id'] as String).set({
      'name': r['name'],
      'kind': r['kind'],
      'isPublic': r['isPublic'],
      'allowedRoles': r['allowedRoles'],
      'createdAt': FieldValue.serverTimestamp(),
      'createdBy': 'seed',
      'membersCount': 0,
      'lastMessage': '',
      'lastAt': FieldValue.serverTimestamp(),
    });
  }
  print('Seeded rooms');
}

