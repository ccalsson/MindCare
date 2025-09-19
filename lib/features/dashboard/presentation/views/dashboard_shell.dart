import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:sami_1/core/constants.dart';
import 'package:sami_1/domain/entities/user.dart';
import 'package:sami_1/shared/providers/session_provider.dart';

class DashboardShell extends StatelessWidget {
  const DashboardShell({required this.navigationShell, required this.destinations, super.key});

  final StatefulNavigationShell navigationShell;
  final List<DashboardDestination> destinations;

  @override
  Widget build(BuildContext context) {
    final session = context.watch<SessionProvider>();
    final isWide = MediaQuery.of(context).size.width >= AppBreakpoints.tablet;
    final allowedDestinations = destinations;

    return Scaffold(
      body: Row(
        children: [
          if (isWide)
            NavigationRail(
              selectedIndex: navigationShell.currentIndex,
              onDestinationSelected: (index) {
                final destination = allowedDestinations[index];
                if (!_canAccess(session, destination)) {
                  _showAccessDenied(context);
                  return;
                }
                navigationShell.goBranch(index);
              },
              labelType: NavigationRailLabelType.all,
              destinations: [
                for (final destination in allowedDestinations)
                  NavigationRailDestination(
                    icon: Icon(destination.icon),
                    selectedIcon: Icon(destination.selectedIcon ?? destination.icon),
                    label: Text(destination.label),
                  ),
              ],
            ),
          Expanded(
            child: Column(
              children: [
                AppBar(
                  title: Text(allowedDestinations[navigationShell.currentIndex].label),
                  actions: [
                    TextButton.icon(
                      onPressed: () async {
                        await session.logout();
                        if (context.mounted) {
                          context.go('/login');
                        }
                      },
                      icon: const Icon(Icons.logout),
                      label: const Text('Salir'),
                    ),
                  ],
                ),
                Expanded(child: navigationShell),
              ],
            ),
          ),
        ],
      ),
      bottomNavigationBar: isWide
          ? null
          : NavigationBar(
              selectedIndex: navigationShell.currentIndex,
              onDestinationSelected: (index) {
                final destination = allowedDestinations[index];
                if (!_canAccess(session, destination)) {
                  _showAccessDenied(context);
                  return;
                }
                navigationShell.goBranch(index);
              },
              destinations: [
                for (final destination in allowedDestinations)
                  NavigationDestination(
                    icon: Icon(destination.icon),
                    selectedIcon: Icon(destination.selectedIcon ?? destination.icon),
                    label: Text(destination.label),
                  ),
              ],
            ),
    );
  }

  bool _canAccess(SessionProvider session, DashboardDestination destination) {
    if (destination.allowedRoles == null) {
      return true;
    }
    return session.hasAnyRole(destination.allowedRoles!);
  }

  void _showAccessDenied(BuildContext context) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('No tienes permisos para acceder a esta sección.')),
    );
  }
}

class DashboardDestination {
  const DashboardDestination({
    required this.label,
    required this.icon,
    required this.path,
    this.selectedIcon,
    this.allowedRoles,
  });

  final String label;
  final IconData icon;
  final IconData? selectedIcon;
  final String path;
  final Set<UserRole>? allowedRoles;
}
