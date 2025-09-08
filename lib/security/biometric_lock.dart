<<<<<<< HEAD
import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Provides biometric guarding for sensitive sections like emotions, history and
/// payments. Use [BiometricLock.guard] to wrap navigation or actions that
/// require biometric authentication.
class BiometricLock {
  BiometricLock._();

  static final LocalAuthentication _auth = LocalAuthentication();
  static const FlutterSecureStorage _storage = FlutterSecureStorage();

  /// Executes [onPassed] only if biometric auth succeeds. If the device has no
  /// biometrics enrolled, [onPassed] is executed immediately.
  static Future<void> guard(
    BuildContext context, {
    required VoidCallback onPassed,
  }) async {
    final canCheck = await _auth.canCheckBiometrics;
    if (!canCheck) {
      onPassed();
      return;
    }
    final didAuth = await _auth.authenticate(
      localizedReason: 'Authenticate to continue',
      options: const AuthenticationOptions(biometricOnly: true),
    );
    if (didAuth) {
      onPassed();
    }
  }

  /// Example secure session storage helpers.
  static Future<void> storeSession(String token) =>
      _storage.write(key: 'session_token', value: token);

  static Future<String?> readSession() =>
      _storage.read(key: 'session_token');

  static Future<void> clear() => _storage.delete(key: 'session_token');
=======
import 'package:local_auth/local_auth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Handles biometric authentication and secure token storage.
class BiometricLock {
  final _auth = LocalAuthentication();
  final _storage = const FlutterSecureStorage();

  Future<bool> authenticate() async {
    final available = await _auth.canCheckBiometrics;
    if (!available) return false;
    return _auth.authenticate(
      localizedReason: 'Autenticate to access sensitive data',
      options: const AuthenticationOptions(biometricOnly: true),
    );
  }

  Future<void> storeSession(String token) =>
      _storage.write(key: 'session_token', value: token);

  Future<String?> readSession() => _storage.read(key: 'session_token');

  Future<void> clear() => _storage.delete(key: 'session_token');
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
}
