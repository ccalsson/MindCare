import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class SupabaseService {
  static bool _initialized = false;
  static bool _configChecked = false;
  static bool _hasConfig = false;

  static SupabaseClient get client => Supabase.instance.client;

  static Future<void> init() async {
    if (_initialized) return;
    final url = (dotenv.maybeGet('SUPABASE_URL')?.trim().isNotEmpty == true
            ? dotenv.maybeGet('SUPABASE_URL')
            : const String.fromEnvironment('SUPABASE_URL', defaultValue: ''))
        ?.trim();
    final anon = (dotenv.maybeGet('SUPABASE_ANON_KEY')?.trim().isNotEmpty == true
            ? dotenv.maybeGet('SUPABASE_ANON_KEY')
            : const String.fromEnvironment('SUPABASE_ANON_KEY', defaultValue: ''))
        ?.trim();
    _configChecked = true;
    _hasConfig = _valid(url) && _valid(anon);
    if (!_hasConfig) {
      // Do not initialize with placeholders; caller can check hasConfig
      throw StateError('Supabase no configurado: define SUPABASE_URL y SUPABASE_ANON_KEY en .env');
    }
    await Supabase.initialize(url: url!, anonKey: anon!);
    _initialized = true;
  }

  static bool get isInitialized => _initialized;
  static bool get hasConfig {
    if (_configChecked) return _hasConfig;
    final url = (dotenv.maybeGet('SUPABASE_URL')?.trim().isNotEmpty == true
            ? dotenv.maybeGet('SUPABASE_URL')
            : const String.fromEnvironment('SUPABASE_URL', defaultValue: ''))
        ?.trim();
    final anon = (dotenv.maybeGet('SUPABASE_ANON_KEY')?.trim().isNotEmpty == true
            ? dotenv.maybeGet('SUPABASE_ANON_KEY')
            : const String.fromEnvironment('SUPABASE_ANON_KEY', defaultValue: ''))
        ?.trim();
    return _valid(url) && _valid(anon);
  }

  static bool _valid(String? v) {
    if (v == null) return false;
    final s = v.trim().toLowerCase();
    if (s.isEmpty) return false;
    if (s.startsWith('https://your-project.supabase.co')) return false;
    if (s == 'your_anon_key') return false;
    return true;
  }
}
