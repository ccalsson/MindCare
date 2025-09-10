import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/hotline.dart';
import '../services/hotlines_service.dart';

class HelpNowSheet extends StatelessWidget {
  final String countryCode;
  const HelpNowSheet({super.key, required this.countryCode});

  @override
  Widget build(BuildContext context) {
    final service = HotlinesService();
    return FutureBuilder<List<Hotline>>(
      future: service.getHotlines(countryCode: countryCode),
      builder: (context, snap) {
        final hotlines = snap.data ?? const <Hotline>[];
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Ayuda inmediata', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                if (hotlines.isEmpty) const Text('No hay líneas disponibles.'),
                ...hotlines.map((h) => ListTile(
                      title: Text(h.name),
                      subtitle: Text(h.phone ?? h.url ?? ''),
                      trailing: IconButton(
                        icon: const Icon(Icons.copy),
                        onPressed: () {
                          final text = h.phone ?? h.url ?? '';
                          Clipboard.setData(ClipboardData(text: text));
                          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Copiado')));
                        },
                      ),
                      onTap: () {
                        // TODO(ccalsson): abrir tel:// o URL con url_launcher
                      },
                    )),
              ],
            ),
          ),
        );
      },
    );
  }
}

