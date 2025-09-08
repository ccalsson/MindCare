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
}
