import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/foundation.dart';

class RoleProvider extends ChangeNotifier {
  String? _role; // 'user' | 'professional'
  bool _loading = false;
  String? get role => _role;
  bool get loading => _loading;

  Future<void> load() async {
    if (_loading) return;
    _loading = true;
    notifyListeners();
    try {
      final uid = FirebaseAuth.instance.currentUser?.uid;
      if (uid == null) {
        _role = null;
      } else {
        final doc = await FirebaseFirestore.instance.collection('users').doc(uid).get();
        _role = (doc.data() ?? const {})['role'] as String?;
      }
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> setRole(String role) async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid != null) {
      await FirebaseFirestore.instance.collection('users').doc(uid).set({'role': role}, SetOptions(merge: true));
    }
    _role = role;
    notifyListeners();
  }
}

