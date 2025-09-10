import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import '../../models/room.dart';
import '../../services/chat_service.dart';
import 'chat_page.dart';

class RoomsListPage extends StatelessWidget {
  const RoomsListPage({super.key});

  @override
  Widget build(BuildContext context) {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    return Scaffold(
      appBar: AppBar(title: const Text('Salas de chat')),
      body: StreamBuilder<DocumentSnapshot<Map<String, dynamic>>>(
        stream: FirebaseFirestore.instance.collection('users').doc(uid).snapshots(),
        builder: (context, snap) {
          final roles = (snap.data?.data()?['roles'] as List?)?.map((e) => e.toString()).toList() ?? const [];
          return StreamBuilder<List<Room>>(
            stream: ChatService().watchVisibleRooms(roles),
            builder: (context, roomsSnap) {
              if (!roomsSnap.hasData) return const Center(child: CircularProgressIndicator());
              final rooms = roomsSnap.data!;
              if (rooms.isEmpty) return const Center(child: Text('No hay salas disponibles'));
              return ListView.separated(
                itemCount: rooms.length,
                separatorBuilder: (_, __) => const Divider(height: 1),
                itemBuilder: (context, i) {
                  final r = rooms[i];
                  return ListTile(
                    title: Text(r.name),
                    subtitle: Text(r.lastMessage ?? ''),
                    trailing: Text(r.membersCount.toString()),
                    onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => ChatPage(roomId: r.id, roomName: r.name))),
                  );
                },
              );
            },
          );
        },
      ),
    );
  }
}

