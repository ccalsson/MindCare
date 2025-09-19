import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:sami_1/core/constants.dart';
import 'package:sami_1/domain/entities/user.dart';
import 'package:sami_1/features/admin/roles/presentation/views/admin_roles_view.dart';
import 'package:sami_1/features/admin/users/presentation/views/admin_users_view.dart';
import 'package:sami_1/features/alerts/presentation/views/alerts_view.dart';
import 'package:sami_1/features/auth/presentation/views/login_view.dart';
import 'package:sami_1/features/auth/presentation/views/register_view.dart';
import 'package:sami_1/features/auth/presentation/views/welcome_view.dart';
import 'package:sami_1/features/cameras/presentation/views/cameras_view.dart';
import 'package:sami_1/features/dashboard/presentation/views/dashboard_shell.dart';
import 'package:sami_1/features/dashboard/presentation/views/dashboard_view.dart';
import 'package:sami_1/features/fuel/presentation/views/fuel_view.dart';
import 'package:sami_1/features/operators/presentation/views/operators_view.dart';
import 'package:sami_1/features/profile/presentation/views/profile_view.dart';
import 'package:sami_1/features/projects/presentation/views/projects_view.dart';
import 'package:sami_1/features/reports/presentation/views/reports_view.dart';
import 'package:sami_1/features/settings/presentation/views/settings_view.dart';
import 'package:sami_1/features/tools/presentation/views/tools_view.dart';
import 'package:sami_1/shared/providers/session_provider.dart';

class AppRouter {
  AppRouter({required SessionProvider sessionProvider}) : _sessionProvider = sessionProvider;

  final SessionProvider _sessionProvider;

  late final List<DashboardDestination> _destinations = [
    const DashboardDestination(label: 'Inicio', icon: Icons.dashboard, path: '/dashboard'),
    const DashboardDestination(label: 'Alertas', icon: Icons.notifications_active, path: '/alerts'),
    const DashboardDestination(label: 'Cámaras', icon: Icons.videocam, path: '/cameras'),
    const DashboardDestination(label: 'Combustible', icon: Icons.local_gas_station, path: '/fuel'),
    const DashboardDestination(label: 'Herramientas', icon: Icons.handyman, path: '/tools'),
    const DashboardDestination(label: 'Operarios', icon: Icons.engineering, path: '/operators'),
    const DashboardDestination(label: 'Proyectos', icon: Icons.assignment, path: '/projects'),
    const DashboardDestination(label: 'Reportes', icon: Icons.bar_chart, path: '/reports'),
    const DashboardDestination(label: 'Ajustes', icon: Icons.settings, path: '/settings'),
    const DashboardDestination(label: 'Perfil', icon: Icons.account_circle, path: '/profile'),
    DashboardDestination(
      label: 'Admin',
      icon: Icons.admin_panel_settings,
      path: '/admin/users',
      allowedRoles: {UserRole.admin},
    ),
  ];

  late final GoRouter router = GoRouter(
    initialLocation: '/welcome',
    refreshListenable: _sessionProvider,
    redirect: (context, state) {
      final loggedIn = _sessionProvider.isAuthenticated;
      final location = state.uri.toString();
      final isAuthRoute = location == '/welcome' || location == '/login' || location == '/register';

      if (!loggedIn && !isAuthRoute) {
        return '/welcome';
      }
      if (loggedIn && isAuthRoute) {
        return '/dashboard';
      }
      if (location.startsWith('/admin') && !_sessionProvider.hasRole(UserRole.admin)) {
        return '/dashboard';
      }
      return null;
    },
    routes: [
      GoRoute(path: '/welcome', builder: (context, state) => const WelcomeView()),
      GoRoute(path: '/login', builder: (context, state) => const LoginView()),
      GoRoute(path: '/register', builder: (context, state) => const RegisterView()),
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) {
          return DashboardShell(
            navigationShell: navigationShell,
            destinations: _destinations,
          );
        },
        branches: [
          StatefulShellBranch(routes: [
            GoRoute(
              path: '/dashboard',
              builder: (context, state) => const DashboardView(),
            ),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/alerts', builder: (context, state) => const AlertsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/cameras', builder: (context, state) => const CamerasView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/fuel', builder: (context, state) => const FuelView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/tools', builder: (context, state) => const ToolsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/operators', builder: (context, state) => const OperatorsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/projects', builder: (context, state) => const ProjectsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/reports', builder: (context, state) => const ReportsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/settings', builder: (context, state) => const SettingsView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(path: '/profile', builder: (context, state) => const ProfileView()),
          ]),
          StatefulShellBranch(routes: [
            GoRoute(
              path: '/admin/users',
              builder: (context, state) => const AdminUsersView(),
            ),
            GoRoute(
              path: '/admin/roles',
              builder: (context, state) => const AdminRolesView(),
            ),
          ]),
        ],
      ),
    ],
  );
}
