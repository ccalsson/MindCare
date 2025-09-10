import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import '../../../models/message.dart';

class MessageBubble extends StatelessWidget {
  final Message m;
  final bool isMe;
  const MessageBubble({super.key, required this.m, required this.isMe});

  @override
  Widget build(BuildContext context) {
    final bg = isMe ? const Color(0xFF25D366) : Theme.of(context).colorScheme.surfaceVariant;
    final fg = isMe ? Colors.white : Theme.of(context).colorScheme.onSurfaceVariant;
    return Align(
      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        constraints: const BoxConstraints(maxWidth: 420),
        margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
        padding: const EdgeInsets.all(10),
        decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(12)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (m.attachments.isNotEmpty)
              ...m.attachments.map((a) => _AttachmentView(url: a.url, mime: a.mime)),
            if (m.text.isNotEmpty) Text(m.text, style: TextStyle(color: fg)),
            if (m.needsReview)
              Text('En revisión', style: TextStyle(color: fg.withOpacity(0.8), fontStyle: FontStyle.italic, fontSize: 12)),
          ],
        ),
      ),
    );
  }
}

class _AttachmentView extends StatelessWidget {
  final String url;
  final String mime;
  const _AttachmentView({required this.url, required this.mime});

  @override
  Widget build(BuildContext context) {
    if (mime.startsWith('image/')) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: CachedNetworkImage(imageUrl: url, width: 240, height: 240, fit: BoxFit.cover),
      );
    }
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        const Icon(Icons.attach_file, size: 16, color: Colors.white),
        const SizedBox(width: 6),
        Flexible(child: Text('Adjunto', style: const TextStyle(color: Colors.white))),
      ],
    );
  }
}

