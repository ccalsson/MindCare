import 'package:cloud_firestore/cloud_firestore.dart';
import '../models/event_entry.dart';

class EventsRepo {
  final FirebaseFirestore _db;
  EventsRepo({FirebaseFirestore? db}) : _db = db ?? FirebaseFirestore.instance;

  Stream<List<EventEntry>> streamUserEvents(String uid) {
    final col = _db.collection('users').doc(uid).collection('events').orderBy('createdAt', descending: true).limit(50);
    return col.snapshots().map((snap) => snap.docs
        .map((d) => EventEntry.fromMap(d.data() as Map<String, dynamic>))
        .toList());
  }

  Future<void> addEvent(String uid, EventEntry e) async {
    await _db.collection('users').doc(uid).collection('events').add(e.toMap());
  }
}

