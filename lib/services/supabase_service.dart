import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class SupabaseService {
  static bool _initialized = false;

  static SupabaseClient get client => Supabase.instance.client;

  static Future<void> init() async {
    if (_initialized) return;
    final url = dotenv.env['SUPABASE_URL'];
    final anon = dotenv.env['SUPABASE_ANON_KEY'];
    if (url == null || anon == null) {
      // Silent no-op if not configured
      return;
    }
    await Supabase.initialize(url: url, anonKey: anon);
    _initialized = true;
  }
}

