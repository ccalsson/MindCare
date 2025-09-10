import 'dart:io';
import 'package:flutter/material.dart';

class AttachmentPreview extends StatelessWidget {
  final File? image;
  final String? fileName;
  const AttachmentPreview({super.key, this.image, this.fileName});

  @override
  Widget build(BuildContext context) {
    if (image != null) {
      return Stack(
        children: [
          Image.file(image!, height: 120),
          Positioned(
            right: 0,
            top: 0,
            child: Container(color: Colors.black45, padding: const EdgeInsets.all(4), child: const Icon(Icons.close, color: Colors.white)),
          ),
        ],
      );
    }
    if (fileName != null) {
      return Container(
        height: 60,
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(color: Colors.grey.shade200, borderRadius: BorderRadius.circular(8)),
        child: Row(children: [const Icon(Icons.file_present), const SizedBox(width: 8), Expanded(child: Text(fileName!))]),
      );
    }
    return const SizedBox.shrink();
  }
}

