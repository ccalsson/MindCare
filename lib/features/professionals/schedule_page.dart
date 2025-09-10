import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'call_waiting_page.dart';

class SchedulePage extends StatelessWidget {
  const SchedulePage({super.key});

  @override
  Widget build(BuildContext context) {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    final stream = FirebaseFirestore.instance
        .collection('appointments')
        .where('userId', isEqualTo: uid)
        .orderBy('startAt')
        .snapshots();
    return Scaffold(
      appBar: AppBar(title: const Text('Mis turnos')),
      body: StreamBuilder<QuerySnapshot>(
        stream: stream,
        builder: (ctx, snap) {
          if (!snap.hasData) return const Center(child: CircularProgressIndicator());
          final docs = snap.data!.docs;
          if (docs.isEmpty) return const Center(child: Text('Sin turnos'));
          return ListView.builder(
            itemCount: docs.length,
            itemBuilder: (_, i) {
              final data = docs[i].data()! as Map<String, dynamic>;
              final startAt = (data['startAt'] as Timestamp?)?.toDate();
              final proId = data['proId']?.toString() ?? '';
              final plan = data['planTier']?.toString();
              return ListTile(
                title: Text('Profesional: $proId'),
                subtitle: Text('Estado: ${data['status']} • Plan: ${plan ?? '-'}'),
                trailing: Text('${startAt ?? ''}'),
                onTap: () {
                  Navigator.of(context).push(MaterialPageRoute(
                    builder: (_) => CallWaitingPage(
                      appointmentId: docs[i].id,
                      proId: proId,
                      planTier: plan,
                    ),
                  ));
                },
              );
            },
          );
        },
      ),
    );
  }
}

