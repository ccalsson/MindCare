import 'package:firebase_auth/firebase_auth.dart';

/// Basic helpers for enrolling/verifying multi-factor auth.
class MfaFlow {
  final FirebaseAuth _auth = FirebaseAuth.instance;

  Future<void> enrollPhone(String phoneNumber) async {
    // TODO: Trigger SMS enrollment.
  }

  Future<void> verifySms(String verificationId, String code) async {
    // TODO: Complete second factor verification.
  }
}
