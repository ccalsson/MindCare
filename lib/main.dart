import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:firebase_auth/firebase_auth.dart';
import 'package:mindcare/utils/context_provider.dart';
import 'package:mindcare/screens/login_screen.dart';
import 'package:mindcare/screens/home_screen.dart';
import 'package:mindcare/services/supabase_service.dart';
import 'package:mindcare/screens/welcome_screen.dart';

Future<void> main() async {
  final widgetsBinding = WidgetsFlutterBinding.ensureInitialized();
  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);
  try {
    await dotenv.load(fileName: ".env");
    // Try initializing Firebase using native configs; if not available, try env-based options (useful for web)
    try {
      await Firebase.initializeApp();
    } catch (_) {
      final opts = _firebaseOptionsFromEnv();
      if (opts != null) {
        await Firebase.initializeApp(options: opts);
      } else if (kIsWeb) {
        // On web, options are required; if missing, we proceed without Firebase
      }
    }
    await SupabaseService.init();
  } catch (_) {}
  FlutterNativeSplash.remove();
  runApp(const MyApp());
}

FirebaseOptions? _firebaseOptionsFromEnv() {
  final apiKey = dotenv.env['FIREBASE_API_KEY'];
  final appId = dotenv.env['FIREBASE_APP_ID'];
  final projectId = dotenv.env['FIREBASE_PROJECT_ID'];
  // Guard: avoid initializing Firebase with placeholder values from .env.sample
  bool _isPlaceholder(String? v) {
    if (v == null) return true;
    final s = v.trim().toLowerCase();
    return s.isEmpty || s.startsWith('your_') || s.contains('your_project');
  }
  if (apiKey == null || appId == null || projectId == null) return null;
  if (_isPlaceholder(apiKey) || _isPlaceholder(appId) || _isPlaceholder(projectId)) {
    return null;
  }
  return FirebaseOptions(
    apiKey: apiKey,
    appId: appId,
    projectId: projectId,
    messagingSenderId: dotenv.env['FIREBASE_MESSAGING_SENDER_ID'] ?? '',
    authDomain: dotenv.env['FIREBASE_AUTH_DOMAIN'],
    storageBucket: dotenv.env['FIREBASE_STORAGE_BUCKET'],
    measurementId: dotenv.env['FIREBASE_MEASUREMENT_ID'],
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    ContextProvider.setContext(context);
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.teal),
        useMaterial3: true,
      ),
      home: const AuthGate(),
    );
  }
}

class AuthGate extends StatelessWidget {
  const AuthGate({super.key});
  @override
  Widget build(BuildContext context) {
    // Fallback if Firebase is not initialized: allow navigating the app (Supabase flows)
    if (Firebase.apps.isEmpty) {
      return const HomeScreen();
    }
    return StreamBuilder<User?>(
      stream: FirebaseAuth.instance.authStateChanges(),
      builder: (context, snap) {
        if (snap.connectionState == ConnectionState.waiting) {
          return const Scaffold(
              body: Center(child: CircularProgressIndicator()));
        }
        if (snap.hasError) {
          return Scaffold(
              body: Center(child: Text('Error de inicio: ${snap.error}')));
        }
        if (snap.data != null) {
          return const HomeScreen();
        }
        return const WelcomeScreen();
      },
    );
  }
}
