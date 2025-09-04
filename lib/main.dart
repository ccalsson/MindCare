import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:go_router/go_router.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'dart:io' show Platform;

// Core services
import 'config/web_optimizations.dart';
import 'core/config/firebase_config.dart';
import 'core/config/remote_config_service.dart';
import 'core/ai/ai_coach_service.dart';
import 'services/stripe_service.dart';

// Router
import 'core/router/app_router.dart';

// Providers
import 'core/providers/auth_provider.dart';
import 'core/providers/subscription_provider.dart';
import 'core/providers/ai_provider.dart';
import 'core/billing/providers/billing_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Cargar variables de entorno
  await dotenv.load(fileName: "env.example");

  // Inicializar Firebase
  FirebaseOptions firebaseOptions;
  if (kIsWeb) {
    firebaseOptions = const FirebaseOptions(
      apiKey: FirebaseConfig.webApiKey,
      authDomain: FirebaseConfig.webAuthDomain,
      projectId: FirebaseConfig.webProjectId,
      storageBucket: FirebaseConfig.webStorageBucket,
      messagingSenderId: FirebaseConfig.webMessagingSenderId,
      appId: FirebaseConfig.webAppId,
    );
  } else if (Platform.isAndroid) {
    firebaseOptions = const FirebaseOptions(
      apiKey: FirebaseConfig.androidApiKey,
      authDomain: FirebaseConfig.androidAuthDomain,
      projectId: FirebaseConfig.androidProjectId,
      storageBucket: FirebaseConfig.androidStorageBucket,
      messagingSenderId: FirebaseConfig.androidMessagingSenderId,
      appId: FirebaseConfig.androidAppId,
    );
  } else if (Platform.isIOS) {
    firebaseOptions = const FirebaseOptions(
      apiKey: FirebaseConfig.iosApiKey,
      authDomain: FirebaseConfig.iosAuthDomain,
      projectId: FirebaseConfig.iosProjectId,
      storageBucket: FirebaseConfig.iosStorageBucket,
      messagingSenderId: FirebaseConfig.iosMessagingSenderId,
      appId: FirebaseConfig.iosAppId,
    );
  } else {
    throw UnsupportedError('Unsupported platform');
  }

  await Firebase.initializeApp(options: firebaseOptions);

  // Inicializar Remote Config
  final remoteConfigService = RemoteConfigService();
  await remoteConfigService.initialize();

  // Optimizaciones para web
  if (kIsWeb) {
    WebOptimizations.configure();
  }

  runApp(MyApp(remoteConfigService: remoteConfigService));
}

class MyApp extends StatelessWidget {
  final RemoteConfigService remoteConfigService;

  const MyApp({super.key, required this.remoteConfigService});

  @override
  Widget build(BuildContext context) {
    ContextProvider.setContext(context);
    return MultiProvider(
      providers: [
        // Remote Config Service
        Provider<RemoteConfigService>.value(
          value: remoteConfigService,
        ),

        // AI Service
        Provider<AiCoachService>(
          create: (_) => AiCoachService(
            apiKey: dotenv.env['OPENAI_API_KEY'] ?? '',
          ),
        ),

        // Stripe Service
        Provider<StripeService>(
          create: (_) => StripeService(
            secretKey: dotenv.env['STRIPE_SECRET_KEY'] ?? '',
            publishableKey: dotenv.env['STRIPE_PUBLISHABLE_KEY'] ?? '',
          ),
        ),

        // Auth Provider
        ChangeNotifierProvider(
          create: (context) => AuthProvider(),
        ),

        // Subscription Provider
        ChangeNotifierProvider(
          create: (context) => SubscriptionProvider(),
        ),

        // AI Provider
        ChangeNotifierProvider(
          create: (context) => AiProvider(),
        ),

        // Billing Provider
        ChangeNotifierProxyProvider<AuthProvider, BillingProvider>(
          create: (context) => BillingProvider(
            context.read<RemoteConfigService>(),
            context.read<StripeService>(),
            context.read<AuthProvider>(),
          ),
          update: (context, auth, previousBilling) => BillingProvider(
            context.read<RemoteConfigService>(),
            context.read<StripeService>(),
            auth,
          ),
        ),
      ],
      child: MaterialApp.router(
        title: 'MindCare',
        debugShowCheckedModeBanner: false,

        // Configuración de localización
        localizationsDelegates: const [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        supportedLocales: const [
          Locale('es', 'ES'),
          Locale('en', 'US'),
        ],
        locale: const Locale('es', 'ES'),

        // Router
        routerConfig: AppRouter.router,

        // Temas
        theme: ThemeData(
          primarySwatch: Colors.blue,
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.blue,
            brightness: Brightness.light,
          ),
          fontFamily: 'Inter',
          pageTransitionsTheme: const PageTransitionsTheme(
            builders: {
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
          fontFamily: 'Inter',
        ),
        themeMode: ThemeMode.system,
      ),
    );
  }
}
