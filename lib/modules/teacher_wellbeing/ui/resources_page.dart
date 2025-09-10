import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import '../models/wellbeing_resource.dart';
import '../services/resources_service.dart';

class ResourcesPage extends StatefulWidget {
  const ResourcesPage({super.key});

  @override
  State<ResourcesPage> createState() => _ResourcesPageState();
}

class _ResourcesPageState extends State<ResourcesPage> {
  String? _kind;
  int? _maxDuration;
  final _service = ResourcesService();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Recursos')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                DropdownButton<String?>(
                  value: _kind,
                  hint: const Text('Tipo'),
                  items: const [null, 'articulo', 'audio', 'video']
                      .map((k) => DropdownMenuItem(value: k, child: Text(k == null ? 'Todos' : k)))
                      .toList(),
                  onChanged: (v) => setState(() => _kind = v),
                ),
                const SizedBox(width: 12),
                DropdownButton<int?>(
                  value: _maxDuration,
                  hint: const Text('Duración'),
                  items: const [null, 5, 10, 15]
                      .map((d) => DropdownMenuItem(value: d, child: Text(d == null ? 'Cualquiera' : '≤ $d min')))
                      .toList(),
                  onChanged: (v) => setState(() => _maxDuration = v),
                ),
              ],
            ),
          ),
          Expanded(
            child: StreamBuilder<List<WellbeingResource>>(
              stream: _service.watchResources(kind: _kind, maxDuration: _maxDuration),
              builder: (context, snap) {
                if (!snap.hasData) return const Center(child: CircularProgressIndicator());
                final list = snap.data!;
                if (list.isEmpty) return const Center(child: Text('Sin recursos'));
                return ListView.separated(
                  itemCount: list.length,
                  separatorBuilder: (_, __) => const Divider(height: 1),
                  itemBuilder: (context, i) {
                    final r = list[i];
                    return ListTile(
                      title: Text(r.title),
                      subtitle: Text('${r.kind} · ${r.durationMin} min'),
                      trailing: const Icon(Icons.open_in_new),
                      onTap: () => _openResource(r),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _openResource(WellbeingResource r) async {
    var url = r.contentUrl;
    if (url.startsWith('gs://')) {
      final ref = FirebaseStorage.instance.refFromURL(url);
      url = await ref.getDownloadURL();
    }
    // TODO(ccalsson): abrir en navegador o reproductor in-app
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Abrir $url')));
  }
}

