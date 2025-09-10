import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:firebase_auth/firebase_auth.dart';
import 'package:mindcare/utils/context_provider.dart';
// import 'package:mindcare/screens/login_screen.dart';
import 'package:mindcare/screens/home_screen.dart';
import 'package:mindcare/services/supabase_service.dart';
import 'package:provider/provider.dart';
import 'providers/membership_provider.dart';
import 'providers/role_provider.dart';
import 'providers/professionals_provider.dart';
import 'providers/appointments_provider.dart';
import 'package:mindcare/screens/welcome_screen.dart';
import 'modules/teacher_wellbeing/state/teacher_wellbeing_controller.dart';
import 'features/student/student_controller.dart';

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
  String _read(String key) {
    final v = dotenv.maybeGet(key);
    if (v != null && v.trim().isNotEmpty) return v;
    // Allow passing from build via --dart-define
    switch (key) {
      case 'FIREBASE_API_KEY':
        return const String.fromEnvironment('FIREBASE_API_KEY', defaultValue: '');
      case 'FIREBASE_APP_ID':
        return const String.fromEnvironment('FIREBASE_APP_ID', defaultValue: '');
      case 'FIREBASE_PROJECT_ID':
        return const String.fromEnvironment('FIREBASE_PROJECT_ID', defaultValue: '');
      case 'FIREBASE_MESSAGING_SENDER_ID':
        return const String.fromEnvironment('FIREBASE_MESSAGING_SENDER_ID', defaultValue: '');
      case 'FIREBASE_AUTH_DOMAIN':
        return const String.fromEnvironment('FIREBASE_AUTH_DOMAIN', defaultValue: '');
      case 'FIREBASE_STORAGE_BUCKET':
        return const String.fromEnvironment('FIREBASE_STORAGE_BUCKET', defaultValue: '');
      case 'FIREBASE_MEASUREMENT_ID':
        return const String.fromEnvironment('FIREBASE_MEASUREMENT_ID', defaultValue: '');
      default:
        return '';
    }
  }
  final apiKey = _read('FIREBASE_API_KEY');
  final appId = _read('FIREBASE_APP_ID');
  final projectId = _read('FIREBASE_PROJECT_ID');
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
    messagingSenderId: _read('FIREBASE_MESSAGING_SENDER_ID'),
    authDomain: _read('FIREBASE_AUTH_DOMAIN').isEmpty ? null : _read('FIREBASE_AUTH_DOMAIN'),
    storageBucket: _read('FIREBASE_STORAGE_BUCKET').isEmpty ? null : _read('FIREBASE_STORAGE_BUCKET'),
    measurementId: _read('FIREBASE_MEASUREMENT_ID').isEmpty ? null : _read('FIREBASE_MEASUREMENT_ID'),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    ContextProvider.setContext(context);
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => MembershipProvider()),
        ChangeNotifierProvider(create: (_) => RoleProvider()),
        ChangeNotifierProvider(create: (_) => ProfessionalsProvider()),
        ChangeNotifierProvider(create: (_) => AppointmentsProvider()),
        ChangeNotifierProvider(create: (_) => TeacherWellbeingController()),
        ChangeNotifierProvider(create: (_) => StudentController()..loadMock()),
      ],
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.teal),
          useMaterial3: true,
        ),
        home: const AuthGate(),
      ),
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
