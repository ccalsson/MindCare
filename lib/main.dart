import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'screens/meditation_screen.dart';
import 'viewmodels/meditation_viewmodel.dart';
import 'services/meditation_repository.dart';
import 'services/analytics_service.dart';
import 'widgets/loading_screen.dart';
import 'package:firebase_core/firebase_core.dart';
import 'config/firebase_config.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  await Firebase.initializeApp(
    options: const FirebaseOptions(
      apiKey: FirebaseConfig.apiKey,
      authDomain: FirebaseConfig.authDomain,
      projectId: FirebaseConfig.projectId,
      storageBucket: FirebaseConfig.storageBucket,
      messagingSenderId: FirebaseConfig.messagingSenderId,
      appId: FirebaseConfig.appId,
    ),
  );
  
  // Optimizaciones para web
  if (kIsWeb) {
    // Desactivar el banner de debug en web para mejorar rendimiento
    debugShowCheckedModeBanner = false;
    
    // Configurar opciones de renderizado para web
    // Esto mejora el rendimiento en navegadores
    Paint.enableDithering = true;
  }
  
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        Provider(
          create: (_) => MeditationRepository(
            baseUrl: 'https://api.mindcare.com',
          ),
        ),
        Provider(
          create: (_) => AnalyticsService(),
        ),
        ChangeNotifierProxyProvider2<MeditationRepository, AnalyticsService, MeditationViewModel>(
          create: (context, repository, analytics) => MeditationViewModel(
            repository ?? MeditationRepository(baseUrl: 'https://api.mindcare.com'),
            analytics ?? AnalyticsService(),
          ),
          update: (context, repository, analytics, previous) => 
            previous ?? MeditationViewModel(repository, analytics),
        ),
      ],
      child: MaterialApp(
        title: 'MindCare',
        debugShowCheckedModeBanner: !kIsWeb, // Ocultar banner en web
        theme: ThemeData(
          primarySwatch: Colors.blue,
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.blue,
            brightness: Brightness.light,
          ),
          // Optimizaciones para web
          pageTransitionsTheme: PageTransitionsTheme(
            builders: {
              // Usar transiciones más ligeras en web
              TargetPlatform.web: FadeUpwardsPageTransitionsBuilder(),
              TargetPlatform.android: ZoomPageTransitionsBuilder(),
              TargetPlatform.iOS: CupertinoPageTransitionsBuilder(),
            },
          ),
        ),
        darkTheme: ThemeData(
          primarySwatch: Colors.blue,
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.blue,
            brightness: Brightness.dark,
          ),
        ),
        themeMode: ThemeMode.system,
        home: kIsWeb 
            ? const WebOptimizedApp() 
            : const MindCareApp(),
      ),
    );
  }
}

// Versión optimizada para web con carga progresiva
class WebOptimizedApp extends StatefulWidget {
  const WebOptimizedApp({Key? key}) : super(key: key);

  @override
  State<WebOptimizedApp> createState() => _WebOptimizedAppState();
}

class _WebOptimizedAppState extends State<WebOptimizedApp> {
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _initializeApp();
  }

  Future<void> _initializeApp() async {
    // Simular carga de recursos para web
    await Future.delayed(const Duration(milliseconds: 800));
    if (mounted) {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const LoadingScreen();
    }
    
    return MeditationScreen(
      onNavigateBack: () {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Volviendo al Dashboard')),
        );
      },
    );
  }
}

class MindCareApp extends StatelessWidget {
  const MindCareApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MeditationScreen(
      onNavigateBack: () {
        // En una app real, esto navegaría de vuelta a la pantalla anterior
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Volviendo al Dashboard')),
        );
      },
    );
  }
}
