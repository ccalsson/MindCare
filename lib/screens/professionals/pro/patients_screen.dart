import 'package:flutter/material.dart';
import 'patient_detail_screen.dart';

class PatientsScreen extends StatelessWidget {
  const PatientsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: 5,
        itemBuilder: (_, i) => ListTile(
          leading: const CircleAvatar(child: Icon(Icons.person)),
          title: Text('Paciente #$i'),
          subtitle: const Text('Hacé tap para ver historia clínica'),
          trailing: const Icon(Icons.chevron_right),
          onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => PatientDetailScreen(userId: 'u$i'))),
        ),
      ),
    );
  }
}

