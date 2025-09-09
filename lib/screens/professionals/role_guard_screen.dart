import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../providers/role_provider.dart';
import 'user/directory_screen.dart';
import 'pro/dashboard_screen.dart';

class RoleGuardScreen extends StatefulWidget {
  const RoleGuardScreen({super.key});

  @override
  State<RoleGuardScreen> createState() => _RoleGuardScreenState();
}

class _RoleGuardScreenState extends State<RoleGuardScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() => context.read<RoleProvider>().load());
  }

  @override
  Widget build(BuildContext context) {
    final roleProv = context.watch<RoleProvider>();
    final role = roleProv.role;

    if (roleProv.loading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (role == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Profesionales')),
        body: Center(
          child: ElevatedButton(
            onPressed: () async {
              final r = await showDialog<String>(
                context: context,
                builder: (_) => const RoleChooserDialog(),
              );
              if (r != null) {
                await context.read<RoleProvider>().setRole(r);
                setState(() {});
              }
            },
            child: const Text('Elegir rol'),
          ),
        ),
      );
    }

    if (role == 'professional') {
      return const ProfessionalDashboardScreen();
    }

    // 'user' → Directory
    return const ProfessionalDirectoryScreen();
  }
}

class RoleChooserDialog extends StatelessWidget {
  const RoleChooserDialog({super.key});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('Elegí tu rol', style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
      content: Text('Podés cambiarlo más tarde en tu perfil.', style: GoogleFonts.poppins()),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context, 'user'),
          child: const Text('Soy Usuario'),
        ),
        FilledButton(
          onPressed: () => Navigator.pop(context, 'professional'),
          child: const Text('Soy Profesional'),
        ),
      ],
    );
  }
}
