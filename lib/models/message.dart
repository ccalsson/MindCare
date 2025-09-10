class MessageAttachment {
  final String url;
  final String storagePath;
  final String mime;
  final int size;

  const MessageAttachment({
    required this.url,
    required this.storagePath,
    required this.mime,
    required this.size,
  });

  Map<String, dynamic> toJson() => {
        'url': url,
        'storagePath': storagePath,
        'mime': mime,
        'size': size,
      };

  factory MessageAttachment.fromJson(Map<String, dynamic> j) => MessageAttachment(
        url: j['url'] as String,
        storagePath: j['storagePath'] as String,
        mime: j['mime'] as String,
        size: (j['size'] as num).toInt(),
      );
}

class Message {
  final String id;
  final String senderId;
  final String text;
  final String type; // text|image|file|voice
  final DateTime? createdAt;
  final DateTime? editedAt;
  final bool deleted;
  final List<String> mentions;
  final List<MessageAttachment> attachments;
  final bool needsReview;

  const Message({
    required this.id,
    required this.senderId,
    required this.text,
    required this.type,
    this.createdAt,
    this.editedAt,
    this.deleted = false,
    this.mentions = const [],
    this.attachments = const [],
    this.needsReview = false,
  });

  Map<String, dynamic> toJson() => {
        'senderId': senderId,
        'text': text,
        'type': type,
        if (createdAt != null) 'createdAt': createdAt!.toIso8601String(),
        if (editedAt != null) 'editedAt': editedAt!.toIso8601String(),
        'deleted': deleted,
        'mentions': mentions,
        'attachments': attachments.map((a) => a.toJson()).toList(),
        'needsReview': needsReview,
      };

  factory Message.fromJson(Map<String, dynamic> j, String id) => Message(
        id: id,
        senderId: j['senderId'] as String,
        text: (j['text'] as String?) ?? '',
        type: j['type'] as String,
        createdAt: (j['createdAt'] != null) ? DateTime.tryParse(j['createdAt'].toString()) : null,
        editedAt: (j['editedAt'] != null) ? DateTime.tryParse(j['editedAt'].toString()) : null,
        deleted: (j['deleted'] as bool?) ?? false,
        mentions: (j['mentions'] as List?)?.map((e) => e.toString()).toList() ?? const [],
        attachments: (j['attachments'] as List?)
                ?.map((e) => MessageAttachment.fromJson(Map<String, dynamic>.from(e)))
                .toList() ??
            const [],
        needsReview: (j['needsReview'] as bool?) ?? false,
      );
}

