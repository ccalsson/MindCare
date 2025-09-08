import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:mindcare/screens/home_screen.dart';
import 'package:mindcare/screens/bienestar_screen.dart';
import 'package:mindcare/screens/tda_tdh_screen.dart';
import 'package:mindcare/screens/estudiantil_screen.dart';
import 'package:mindcare/screens/desarrollo_profesional_screen.dart';
import 'package:mindcare/screens/profesionales_screen.dart';
import 'package:mindcare/screens/profesional_detail_screen.dart';
import 'package:mindcare/screens/booking_screen.dart';
import 'package:mindcare/core/billing/screens/paywall_screen.dart';
import 'package:mindcare/screens/settings_screen.dart';
import 'package:mindcare/screens/error_screen.dart';

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
        ],
        errorBuilder: (context, state) => const ErrorScreen(),
      );
}
