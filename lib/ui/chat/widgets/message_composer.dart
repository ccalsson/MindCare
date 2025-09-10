import 'dart:io';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:record/record.dart';
import '../../../services/chat_service.dart';
import '../../../services/moderation_service.dart';
import '../../../core/routes.dart';
import '../../../features/student/student_home_page.dart';

class MessageComposer extends StatefulWidget {
  final String roomId;
  final ChatService service;
  const MessageComposer({super.key, required this.roomId, required this.service});

  @override
  State<MessageComposer> createState() => _MessageComposerState();
}

class _MessageComposerState extends State<MessageComposer> {
  final _ctrl = TextEditingController();
  final _picker = ImagePicker();
  final _record = AudioRecorder();
  final _mod = ModerationService();
  bool _recording = false;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Row(
        children: [
          IconButton(
            tooltip: 'Imagen',
            icon: const Icon(Icons.photo),
            onPressed: _pickImage,
          ),
          IconButton(
            tooltip: 'Archivo',
            icon: const Icon(Icons.attach_file),
            onPressed: _pickFile,
          ),
          IconButton(
            tooltip: 'Voz',
            icon: Icon(_recording ? Icons.stop : Icons.mic),
            onPressed: _toggleRecord,
          ),
          Expanded(
            child: TextField(
              controller: _ctrl,
              decoration: const InputDecoration(hintText: 'Escribí un mensaje…'),
              minLines: 1,
              maxLines: 5,
            ),
          ),
          IconButton(
            tooltip: 'Enviar',
            icon: const Icon(Icons.send),
            onPressed: _send,
          ),
        ],
      ),
    );
  }

  Future<void> _send() async {
    final text = _ctrl.text.trim();
    if (text.isEmpty) return;
    // Hook leve: sugerir abrir módulo Estudiante ante intención
    if (isStudentIntent(text)) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('¿Querés abrir herramientas para estudio?'),
          action: SnackBarAction(
            label: 'Abrir',
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const StudentHomePage()),
              );
            },
          ),
        ),
      );
    }
    await widget.service.sendText(widget.roomId, text);
    if (!mounted) return;
    _ctrl.clear();
    if (_mod.shouldFlag(text)) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Tu mensaje será revisado por moderación')));
    }
  }

  Future<void> _pickImage() async {
    final x = await _picker.pickImage(source: ImageSource.gallery, imageQuality: 85);
    if (x == null) return;
    await widget.service.sendAttachment(widget.roomId, File(x.path), mime: 'image/${x.path.split('.').last}');
  }

  Future<void> _pickFile() async {
    final r = await FilePicker.platform.pickFiles(withReadStream: false);
    if (r == null || r.files.single.path == null) return;
    final file = File(r.files.single.path!);
    final mime = r.files.single.extension != null ? 'application/${r.files.single.extension}' : 'application/octet-stream';
    await widget.service.sendAttachment(widget.roomId, file, mime: mime);
  }

  Future<void> _toggleRecord() async {
    if (_recording) {
      final path = await _record.stop();
      setState(() => _recording = false);
      if (path != null) {
        await widget.service.sendAttachment(widget.roomId, File(path), mime: 'audio/aac');
      }
    } else {
      final hasPerm = await _record.hasPermission();
      if (!hasPerm) return;
      final path = '${Directory.systemTemp.path}/rec_${DateTime.now().millisecondsSinceEpoch}.m4a';
      await _record.start(const RecordConfig(encoder: AudioEncoder.aacLc), path: path);
      setState(() => _recording = true);
    }
  }
}
