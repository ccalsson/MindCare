import 'package:flutter/material.dart';
import 'agenda_screen.dart';
import 'patients_screen.dart';
import 'availability_screen.dart';
import 'profile_screen.dart';
import 'finances_screen.dart';

class ProfessionalDashboardScreen extends StatefulWidget {
  const ProfessionalDashboardScreen({super.key});

  @override
  State<ProfessionalDashboardScreen> createState() => _ProfessionalDashboardScreenState();
}

class _ProfessionalDashboardScreenState extends State<ProfessionalDashboardScreen> with SingleTickerProviderStateMixin {
  late final TabController _tab;
  final _tabs = const [
    Tab(text: 'Agenda', icon: Icon(Icons.event)),
    Tab(text: 'Pacientes', icon: Icon(Icons.people_outline)),
    Tab(text: 'Disponibilidad', icon: Icon(Icons.schedule)),
    Tab(text: 'Perfil', icon: Icon(Icons.person_outline)),
    Tab(text: 'Finanzas', icon: Icon(Icons.account_balance_wallet_outlined)),
  ];

  @override
  void initState() {
    super.initState();
    _tab = TabController(length: _tabs.length, vsync: this);
  }

  @override
  void dispose() {
    _tab.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Panel del Profesional'),
        bottom: TabBar(controller: _tab, tabs: _tabs, isScrollable: true),
      ),
      body: TabBarView(
        controller: _tab,
        children: const [
          AgendaScreen(),
          PatientsScreen(),
          AvailabilityEditorScreen(),
          ProfileScreen(),
          FinancesScreen(),
        ],
      ),
    );
  }
}

