import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../features/addictions/ui/addictions_intro_screen.dart';
import '../../features/addictions/ui/screening_runner_screen.dart';
import '../../features/addictions/ui/result_and_plan_screen.dart';
import '../../features/addictions/ui/daily_checkin_screen.dart';
import '../../features/addictions/ui/exercises_hub_screen.dart';
import '../../features/addictions/ui/progress_screen.dart';
import '../../features/addictions/ui/help_now_sheet.dart';
import '../../features/addictions/models/screening_result.dart';
import '../../modules/teacher_wellbeing/ui/wellbeing_home_page.dart';
import '../../modules/teacher_wellbeing/ui/checkin_page.dart';
import '../../modules/teacher_wellbeing/ui/routines_page.dart';
import '../../modules/teacher_wellbeing/ui/resources_page.dart';
import '../../modules/teacher_wellbeing/ui/share_preferences_page.dart';
import '../../ui/chat/rooms_list_page.dart';
import '../../ui/chat/chat_page.dart';
import '../routes.dart';
import '../../features/professionals/schedule_page.dart';
import '../../features/professionals/call_waiting_page.dart';

// Importar pantallas (se crearán después)
// import '../modules/bienestar/screens/bienestar_screen.dart';
// import '../modules/tda_tdh/screens/tda_tdh_screen.dart';
// import '../modules/estudiantil/screens/estudiantil_screen.dart';
// import '../modules/desarrollo_profesional/screens/desarrollo_profesional_screen.dart';
// import '../modules/profesionales/screens/profesionales_screen.dart';
// import '../modules/profesionales/screens/profesional_detail_screen.dart';
// import '../modules/profesionales/screens/booking_screen.dart';
// import '../core/billing/screens/paywall_screen.dart';
// import '../core/auth/screens/settings_screen.dart';

class AppRouter {
  static const String home = '/';
  static const String bienestar = '/bienestar';
  static const String tdaTdh = '/tda-tdah';
  static const String estudiantil = '/estudiantil';
  static const String desarrolloProfesional = '/profesional';
  static const String profesionales = '/profesionales';
  static const String profesionalDetail = '/profesionales/:id';
  static const String booking = '/booking/:id';
  static const String paywall = '/paywall';
  static const String settings = '/settings';

  static GoRouter get router => GoRouter(
        initialLocation: home,
        routes: [
          GoRoute(
            path: home,
            builder: (context, state) => const HomeScreen(),
          ),
          // Addictions module routes
          GoRoute(
            path: '/addictions',
            builder: (context, state) => const AddictionsIntroScreen(),
          ),
          GoRoute(
            path: '/addictions/screening/:instrument/:version',
            builder: (context, state) {
              final instrument = state.pathParameters['instrument']!;
              final version = int.tryParse(state.pathParameters['version'] ?? '1') ?? 1;
              return ScreeningRunnerScreen(instrument: instrument, version: version);
            },
          ),
          GoRoute(
            path: '/addictions/plan',
            builder: (context, state) {
              final args = state.extra as Map<String, dynamic>?;
              final result = args?['result'] as ScreeningResult?;
              if (result == null) {
                return const ErrorScreen();
              }
              return ResultAndPlanScreen(result: result);
            },
          ),
          GoRoute(
            path: '/addictions/checkin',
            builder: (context, state) => const DailyCheckinScreen(),
          ),
          GoRoute(
            path: '/addictions/progress',
            builder: (context, state) => const ProgressScreen(),
          ),
          GoRoute(
            path: '/addictions/exercises',
            builder: (context, state) => const ExercisesHubScreen(),
          ),
          GoRoute(
            path: '/addictions/help',
            builder: (context, state) => const _HelpNowScaffold(),
          ),
          // Teacher Wellbeing
          GoRoute(
            path: '/teacher/wellbeing/home',
            builder: (context, state) => const WellbeingHomePage(),
          ),
          GoRoute(
            path: '/teacher/wellbeing/checkin',
            builder: (context, state) => const CheckinPage(),
          ),
          GoRoute(
            path: '/teacher/wellbeing/routines',
            builder: (context, state) => const RoutinesPage(),
          ),
          GoRoute(
            path: '/teacher/wellbeing/resources',
            builder: (context, state) => const ResourcesPage(),
          ),
          GoRoute(
            path: '/teacher/wellbeing/share',
            builder: (context, state) => const SharePreferencesPage(),
          ),
          // Chat module
          GoRoute(
            path: '/rooms',
            builder: (context, state) => const RoomsListPage(),
          ),
          GoRoute(
            path: '/chat/:roomId',
            builder: (context, state) {
              final roomId = state.pathParameters['roomId']!;
              final name = state.uri.queryParameters['name'] ?? 'Chat';
              return ChatPage(roomId: roomId, roomName: name);
            },
          ),
          // Professionals video calls
          GoRoute(
            path: '/professionals/schedule',
            builder: (context, state) => const SchedulePage(),
          ),
          GoRoute(
            path: '/professionals/call/:appointmentId',
            builder: (context, state) {
              final apptId = state.pathParameters['appointmentId']!;
              final proId = state.uri.queryParameters['proId'] ?? '';
              final plan = state.uri.queryParameters['planTier'];
              return CallWaitingPage(appointmentId: apptId, proId: proId, planTier: plan);
            },
          ),
          GoRoute(
            path: bienestar,
            builder: (context, state) => const BienestarScreen(),
          ),
          GoRoute(
            path: tdaTdh,
            builder: (context, state) => const TdaTdhScreen(),
          ),
          GoRoute(
            path: estudiantil,
            builder: (context, state) => const EstudiantilScreen(),
          ),
          GoRoute(
            path: desarrolloProfesional,
            builder: (context, state) => const DesarrolloProfesionalScreen(),
          ),
          GoRoute(
            path: profesionales,
            builder: (context, state) => const ProfesionalesScreen(),
          ),
          GoRoute(
            path: profesionalDetail,
            builder: (context, state) {
              final professionalId = state.pathParameters['id']!;
              return ProfesionalDetailScreen(professionalId: professionalId);
            },
          ),
          GoRoute(
            path: booking,
            builder: (context, state) {
              final bookingId = state.pathParameters['id']!;
              return BookingScreen(bookingId: bookingId);
            },
          ),
          GoRoute(
            path: paywall,
            builder: (context, state) => const PaywallScreen(),
          ),
          GoRoute(
            path: settings,
            builder: (context, state) => const SettingsScreen(),
          ),
          ...getStudentRoutes(),
        ],
        errorBuilder: (context, state) => const ErrorScreen(),
      );
}

// Pantallas temporales hasta que se implementen las reales
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MindCare - Inicio'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Bienvenido a MindCare',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            const Text('Tu compañero de bienestar mental con IA'),
            const SizedBox(height: 40),
            ElevatedButton(
              onPressed: () => context.go(AppRouter.bienestar),
              child: const Text('Módulo Bienestar'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => context.go('/addictions'),
              child: const Text('Bienestar y Adicciones'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => context.go(AppRouter.paywall),
              child: const Text('Ver Planes'),
            ),
          ],
        ),
      ),
    );
  }
}

class BienestarScreen extends StatelessWidget {
  const BienestarScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bienestar'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Módulo de Bienestar - En desarrollo'),
      ),
    );
  }
}

class TdaTdhScreen extends StatelessWidget {
  const TdaTdhScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('TDA/TDAH'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Módulo TDA/TDAH - En desarrollo'),
      ),
    );
  }
}

class EstudiantilScreen extends StatelessWidget {
  const EstudiantilScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Estudiantil'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Módulo Estudiantil - En desarrollo'),
      ),
    );
  }
}

class DesarrolloProfesionalScreen extends StatelessWidget {
  const DesarrolloProfesionalScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Desarrollo Profesional'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Módulo de Desarrollo Profesional - En desarrollo'),
      ),
    );
  }
}

class ProfesionalesScreen extends StatelessWidget {
  const ProfesionalesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profesionales'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Directorio de Profesionales - En desarrollo'),
      ),
    );
  }
}

class ProfesionalDetailScreen extends StatelessWidget {
  final String professionalId;

  const ProfesionalDetailScreen({
    super.key,
    required this.professionalId,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Perfil del Profesional'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.profesionales),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('ID del Profesional: $professionalId'),
            const SizedBox(height: 20),
            const Text('Perfil del Profesional - En desarrollo'),
          ],
        ),
      ),
    );
  }
}

class BookingScreen extends StatelessWidget {
  final String bookingId;

  const BookingScreen({
    super.key,
    required this.bookingId,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reserva'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.profesionales),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('ID de la Reserva: $bookingId'),
            const SizedBox(height: 20),
            const Text('Pantalla de Reserva - En desarrollo'),
          ],
        ),
      ),
    );
  }
}

class PaywallScreen extends StatelessWidget {
  const PaywallScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Planes y Precios'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Paywall - En desarrollo'),
      ),
    );
  }
}

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Configuración'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go(AppRouter.home),
        ),
      ),
      body: const Center(
        child: Text('Configuración - En desarrollo'),
      ),
    );
  }
}

class ErrorScreen extends StatelessWidget {
  const ErrorScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Error'),
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red,
            ),
            SizedBox(height: 16),
            Text(
              'Página no encontrada',
              style: TextStyle(fontSize: 24),
            ),
            SizedBox(height: 8),
            Text('La página que buscas no existe'),
          ],
        ),
      ),
    );
  }
}

// Simple scaffold wrapper to show HelpNowSheet as a full page
class _HelpNowScaffold extends StatelessWidget {
  const _HelpNowScaffold();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Ayuda inmediata')),
      body: const Padding(
        padding: EdgeInsets.all(8.0),
        child: HelpNowSheet(countryCode: 'AR'),
      ),
    );
  }
}
