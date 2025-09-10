import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/foundation.dart';
import '../models/message.dart';
import '../services/chat_service.dart';

class ChatProvider extends ChangeNotifier {
  final ChatService _service;
  final String roomId;
  List<Message> messages = const [];
  DocumentSnapshot? _lastDoc;
  bool _loadingMore = false;

  ChatProvider({required this.roomId, ChatService? service}) : _service = service ?? ChatService() {
    _service.watchMessages(roomId).listen((list) {
      messages = list;
      if (list.isNotEmpty) {
        // Save last doc ref for pagination when provided via snapshots if needed
      }
      notifyListeners();
    });
  }

  bool get isLoadingMore => _loadingMore;

  Future<void> loadMore() async {
    if (_loadingMore) return;
    _loadingMore = true;
    notifyListeners();
    try {
      // Using REST pagination would require keeping last snapshot; omitted for brevity.
      await Future.delayed(const Duration(milliseconds: 300));
    } finally {
      _loadingMore = false;
      notifyListeners();
    }
  }
}

