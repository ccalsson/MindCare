import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:sign_in_with_apple/sign_in_with_apple.dart';

class AuthSocialService {
  bool get isFirebaseReady => Firebase.apps.isNotEmpty;

  Future<UserCredential> signInWithGoogle() async {
    if (!isFirebaseReady) {
      throw StateError('Firebase no está configurado');
    }
    if (kIsWeb) {
      final provider = GoogleAuthProvider();
      return FirebaseAuth.instance.signInWithPopup(provider);
    }
    final GoogleSignInAccount? googleUser = await GoogleSignIn().signIn();
    if (googleUser == null) {
      throw StateError('Inicio cancelado');
    }
    final googleAuth = await googleUser.authentication;
    final credential = GoogleAuthProvider.credential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );
    return FirebaseAuth.instance.signInWithCredential(credential);
  }

  Future<UserCredential> signInWithApple() async {
    if (!isFirebaseReady) {
      throw StateError('Firebase no está configurado');
    }
    if (!Platform.isIOS && !Platform.isMacOS) {
      throw UnsupportedError('Sign in with Apple solo en iOS/macOS');
    }
    final apple = await SignInWithApple.getAppleIDCredential(
      scopes: [
        AppleIDAuthorizationScopes.email,
        AppleIDAuthorizationScopes.fullName,
      ],
    );
    final oauth = OAuthProvider('apple.com');
    final credential = oauth.credential(
      idToken: apple.identityToken,
      accessToken: apple.authorizationCode,
    );
    return FirebaseAuth.instance.signInWithCredential(credential);
  }
}

