import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_native_splash/flutter_native_splash.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:mindcare/utils/context_provider.dart';
import 'package:mindcare/screens/login_screen.dart';
import 'package:mindcare/screens/home_screen.dart';

Future<void> main() async {
  final widgetsBinding = WidgetsFlutterBinding.ensureInitialized();
  FlutterNativeSplash.preserve(widgetsBinding: widgetsBinding);
  try {
    await dotenv.load(fileName: ".env");
    await Firebase.initializeApp();
  } catch (_) {}
  FlutterNativeSplash.remove();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    ContextProvider.setContext(context);
<<<<<<< HEAD
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
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.teal,
            brightness: Brightness.light,
          ),
          fontFamily: 'Poppins',
          scaffoldBackgroundColor: Colors.teal[50],
          pageTransitionsTheme: const PageTransitionsTheme(
            builders: {
              TargetPlatform.android: ZoomPageTransitionsBuilder(),
              TargetPlatform.iOS: CupertinoPageTransitionsBuilder(),
            },
          ),
        ),
        darkTheme: ThemeData(
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.teal,
            brightness: Brightness.dark,
          ),
          fontFamily: 'Poppins',
        ),
        themeMode: ThemeMode.system,
=======
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.teal),
        useMaterial3: true,
>>>>>>> 34fe70b (chore: fix pubspec merge, add Firebase seed + storage upload scripts, deployable indexes and rules)
      ),
      home: const AuthGate(),
    );
  }
}

class AuthGate extends StatelessWidget {
  const AuthGate({super.key});
  @override
  Widget build(BuildContext context) {
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
        return const LoginScreen();
      },
    );
  }
}
