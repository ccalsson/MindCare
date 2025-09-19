import 'dart:convert';
import 'dart:typed_data';
import 'package:cryptography/cryptography.dart';
import 'package:mindcare/core/security/crypto_aes.dart';
import 'package:mindcare/core/storage/secure_store.dart';

// TODO(migration-crypto): Informe de migración
// - Archivos afectados: lib/security/client_crypto.dart
// - Algoritmo previo: AES-GCM (encrypt), IV de 12 bytes, serialización JSON {iv: base64Url, data: base64(cipher|tag)}
// - Algoritmo nuevo: AES-256-GCM (cryptography), nonce de 12 bytes, serialización Base64 de nonce|cipher|mac
// - Compatibilidad: decrypt() autodetecta legado (JSON) vs nuevo formato (Base64 concatenado)

/// Helper for client-side encryption of sensitive documents.
class ClientCrypto {
  final SecureStore _storage;

  ClientCrypto({SecureStore? storage}) : _storage = storage ?? const FlutterSecureStore();

  Future<String> _ensureKeyB64() async {
    var keyData = await _storage.read(key: 'user_key');
    if (keyData == null) {
      final secret = await AesGcm.with256bits().newSecretKey();
      final bytes = await secret.extractBytes();
      // Guardamos en Base64 URL para mantener compatibilidad con lo previo
      keyData = base64UrlEncode(bytes);
      await _storage.write(key: 'user_key', value: keyData);
    }
    // Acepta claves previas en base64Url o base64 estándar
    List<int> keyBytes;
    try {
      keyBytes = base64Url.decode(keyData);
    } catch (_) {
      keyBytes = base64.decode(keyData);
    }
    return base64Encode(keyBytes); // devolver siempre base64 estándar
  }

  Future<String> encrypt(String plain) async {
    final keyB64 = await _ensureKeyB64();
    final payload = await AesGcmHelper.encryptToBase64(
      keyB64: keyB64,
      plaintext: utf8.encode(plain),
    );
    return payload;
  }

  Future<String> decrypt(String payload) async {
    final keyB64 = await _ensureKeyB64();
    // Autodetección de formato legado (JSON) vs nuevo (Base64 concatenado)
    if (payload.trimLeft().startsWith('{')) {
      final map = jsonDecode(payload) as Map<String, dynamic>;
      final iv = base64Url.decode(map['iv'] as String);
      final cipherB64 = map['data'] as String;
      // encrypt prior concatena cipher|tag en base64, IV va aparte.
      // Convertimos a nuevo formato nonce|cipher|mac (concat) para reutilizar el helper.
      final cipherAll = base64.decode(cipherB64);
      if (cipherAll.length < 16) {
        throw StateError('Encrypted payload inválido');
      }
      final cipher = cipherAll.sublist(0, cipherAll.length - 16);
      final mac = cipherAll.sublist(cipherAll.length - 16);
      final legacyConcat = base64Encode(
        Uint8List(iv.length + cipher.length + mac.length)
          ..setRange(0, iv.length, iv)
          ..setRange(iv.length, iv.length + cipher.length, cipher)
          ..setRange(iv.length + cipher.length, iv.length + cipher.length + mac.length, mac),
      );
      final clear = await AesGcmHelper.decryptFromBase64(
        keyB64: keyB64,
        payloadB64: legacyConcat,
      );
      return utf8.decode(clear);
    } else {
      final clear = await AesGcmHelper.decryptFromBase64(
        keyB64: keyB64,
        payloadB64: payload,
      );
      return utf8.decode(clear);
    }
  }
}
