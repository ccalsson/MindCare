import 'package:supabase_flutter/supabase_flutter.dart';

class SupabaseAuthService {
  final _auth = Supabase.instance.client.auth;

  User? get currentUser => _auth.currentUser;

  Future<AuthResponse> signInWithPassword({required String email, required String password}) async {
    try {
      return await _auth.signInWithPassword(email: email, password: password);
    } on AuthApiException catch (e) {
      if (e.statusCode == 400 || e.statusCode == 422) {
        // Try sign up if user does not exist
        return await _auth.signUp(email: email, password: password);
      }
      rethrow;
    }
  }

  Future<void> signOut() => _auth.signOut();
}

