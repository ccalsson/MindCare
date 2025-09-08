import 'package:firebase_auth/firebase_auth.dart';

/// Basic helpers for enrolling/verifying multi-factor auth.
class MfaFlow {
  Future<void> enrollPhone(String phoneNumber) async {
    // TODO: Trigger SMS enrollment using FirebaseAuth.instance.
  }

  Future<void> verifySms(String verificationId, String code) async {
    // TODO: Complete second factor verification using FirebaseAuth.instance.
  }
}
