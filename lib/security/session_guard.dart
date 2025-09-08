import 'dart:async';
import 'package:firebase_auth/firebase_auth.dart';

/// Tracks inactivity and forces sign-out after [timeout].
class SessionGuard {
  final Duration timeout;
  Timer? _timer;

  SessionGuard({this.timeout = const Duration(minutes: 15)});

  void registerActivity(void Function() onTimeout) {
    _timer?.cancel();
    _timer = Timer(timeout, onTimeout);
  }

  Future<bool> reauthenticate() async {
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) return false;
    // TODO: Implement credential reauthentication flow.
    return true;
  }

  void dispose() {
    _timer?.cancel();
  }
}
