import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/chat_provider.dart';
import '../../services/chat_service.dart';
import 'widgets/message_bubble.dart';
import 'widgets/message_composer.dart';

class ChatPage extends StatelessWidget {
  final String roomId;
  final String roomName;
  const ChatPage({super.key, required this.roomId, required this.roomName});

  @override
  Widget build(BuildContext context) {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    return ChangeNotifierProvider(
      create: (_) => ChatProvider(roomId: roomId),
      child: Scaffold(
        appBar: AppBar(title: Text(roomName)),
        body: Consumer<ChatProvider>(
          builder: (context, p, _) => Column(
            children: [
              Expanded(
                child: ListView.builder(
                  reverse: true,
                  itemCount: p.messages.length + 1,
                  itemBuilder: (context, i) {
                    if (i == p.messages.length) {
                      return p.isLoadingMore
                          ? const Padding(padding: EdgeInsets.all(8), child: Center(child: CircularProgressIndicator()))
                          : TextButton(onPressed: p.loadMore, child: const Text('Cargar más'));
                    }
                    final m = p.messages[i];
                    if (m.needsReview && m.senderId != uid) {
                      return const SizedBox.shrink();
                    }
                    return MessageBubble(m: m, isMe: m.senderId == uid);
                  },
                ),
              ),
              MessageComposer(roomId: roomId, service: ChatService()),
            ],
          ),
        ),
      ),
    );
  }
}

