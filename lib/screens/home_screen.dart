import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import 'package:mindcare/screens/professionals/role_guard_screen.dart';
import 'package:mindcare/screens/my_bookings_screen.dart';
import 'package:mindcare/widgets/module_card.dart';
import 'package:mindcare/providers/membership_provider.dart';
import 'package:mindcare/modules/ansiedad/ansiedad_screen.dart';
import 'package:mindcare/modules/estudiantil/estudiante_screen.dart';
import 'package:mindcare/modules/tda_tdh/tda_tdh_screen.dart';
import 'package:mindcare/modules/desarrollo_profesional/desarrollo_personal_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  Future<void> _logout(BuildContext context) async {
    if (Firebase.apps.isEmpty) return;
    try {
      await FirebaseAuth.instance.signOut();
      if (context.mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Sesión cerrada')));
      }
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    final user = Firebase.apps.isNotEmpty ? FirebaseAuth.instance.currentUser : null;
    final isPremium = context.watch<MembershipProvider>().isActive;

    final poppins = GoogleFonts.poppinsTextTheme(Theme.of(context).textTheme);

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        foregroundColor: Colors.white,
        title: Text('MindCare', style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
        actions: Firebase.apps.isNotEmpty
            ? [
                IconButton(
                  onPressed: () => _logout(context),
                  icon: const Icon(Icons.logout),
                  tooltip: 'Salir',
                ),
              ]
            : null,
      ),
      drawer: _buildDrawer(context, user),
      extendBodyBehindAppBar: true,
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF25D366), Color(0xFF2E86E6)],
          ),
        ),
        child: SafeArea(
          child: DefaultTextStyle.merge(
            style: poppins.bodyMedium!.copyWith(color: Colors.white),
            child: LayoutBuilder(
              builder: (context, constraints) {
                final isWide = constraints.maxWidth >= 1000;
                final gridCrossAxis = isWide ? 3 : 2;
                return SingleChildScrollView(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Header con saludo e imagen
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Hola, ${user?.email ?? 'bienvenido/a'}',
                                  style: GoogleFonts.poppins(
                                    fontSize: 22,
                                    fontWeight: FontWeight.w600,
                                    color: Colors.white,
                                  ),
                                ),
                                const SizedBox(height: 6),
                                Text(
                                  'Soy tu asistente de bienestar. ¿Cómo te puedo ayudar hoy?',
                                  style: GoogleFonts.poppins(fontSize: 14, color: Colors.white.withOpacity(0.95)),
                                ),
                                const SizedBox(height: 12),
                                Row(
                                  children: [
                                    FilledButton.icon(
                                      style: FilledButton.styleFrom(backgroundColor: Colors.white, foregroundColor: const Color(0xFF2E86E6)),
                                      onPressed: () {},
                                      icon: const Icon(Icons.keyboard_alt),
                                      label: const Text('Chat de texto'),
                                    ),
                                    const SizedBox(width: 12),
                                    OutlinedButton.icon(
                                      style: OutlinedButton.styleFrom(foregroundColor: Colors.white, side: const BorderSide(color: Colors.white)),
                                      onPressed: () {},
                                      icon: const Icon(Icons.mic),
                                      label: const Text('Chat por voz'),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: 12),
                          if (isWide)
                            SizedBox(
                              width: 220,
                              child: ClipRRect(
                                borderRadius: BorderRadius.circular(16),
                                child: Image.asset('assets/images/placeholder.png', fit: BoxFit.cover),
                              ),
                            ),
                        ],
                      ),
                      const SizedBox(height: 24),
                      // Grid de módulos
                      Container(
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.08),
                          borderRadius: BorderRadius.circular(16),
                        ),
                        padding: const EdgeInsets.all(12),
                        child: GridView(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: gridCrossAxis,
                            crossAxisSpacing: 12,
                            mainAxisSpacing: 12,
                            childAspectRatio: 1.1,
                          ),
                          children: [
                            ModuleCard(
                              title: 'Manejo de Ansiedad',
                              icon: Icons.self_improvement,
                              color: const Color(0xFF2E86E6),
                              locked: !isPremium,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const AnsiedadScreen())),
                            ),
                            ModuleCard(
                              title: 'Estudiante',
                              icon: Icons.school,
                              color: const Color(0xFF25D366),
                              locked: !isPremium,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const EstudianteScreen())),
                            ),
                            ModuleCard(
                              title: 'TDA/TDH',
                              icon: Icons.bolt,
                              color: Colors.amber.shade700,
                              locked: !isPremium,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const TdaTdhScreen())),
                            ),
                            ModuleCard(
                              title: 'Desarrollo Personal',
                              icon: Icons.psychology,
                              color: Colors.purple,
                              locked: !isPremium,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const DesarrolloPersonalScreen())),
                            ),
                            ModuleCard(
                              title: 'Profesionales',
                              icon: Icons.medical_services_outlined,
                              color: Colors.orange,
                              locked: false,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RoleGuardScreen())),
                            ),
                            ModuleCard(
                              title: 'Mis reservas',
                              icon: Icons.event_available_outlined,
                              color: Colors.teal,
                              locked: false,
                              onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const MyBookingsScreen())),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ),
      ),
      backgroundColor: Colors.transparent,
    );
  }

  Drawer _buildDrawer(BuildContext context, User? user) {
    return Drawer(
      child: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            UserAccountsDrawerHeader(
              accountName: Text(user?.displayName ?? 'Invitado'),
              accountEmail: Text(user?.email ?? 'Sin sesión'),
              currentAccountPicture: const CircleAvatar(child: Icon(Icons.person)),
            ),
            ListTile(
              leading: const Icon(Icons.color_lens_outlined),
              title: const Text('Modo claro/oscuro'),
              onTap: () {},
            ),
            ListTile(
              leading: const Icon(Icons.person_outline),
              title: const Text('Datos personales'),
              onTap: () {},
            ),
            Consumer<MembershipProvider>(
              builder: (context, mem, _) => SwitchListTile(
                secondary: const Icon(Icons.workspace_premium_outlined),
                title: const Text('Membresía activa'),
                value: mem.isActive,
                onChanged: (v) => mem.setActive(v),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.notifications_outlined),
              title: const Text('Notificaciones / Privacidad'),
              onTap: () {},
            ),
            ListTile(
              leading: const Icon(Icons.support_agent),
              title: const Text('Soporte / Ayuda'),
              onTap: () {},
            ),
            const Spacer(),
            if (Firebase.apps.isNotEmpty)
              ListTile(
                leading: const Icon(Icons.logout),
                title: const Text('Cerrar sesión'),
                onTap: () => _logout(context),
              ),
          ],
        ),
      ),
    );
  }
}
