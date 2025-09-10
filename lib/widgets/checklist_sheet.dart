import 'package:flutter/material.dart';

Future<void> showChecklistSheet(BuildContext context) async {
  await showModalBottomSheet(
    context: context,
    isScrollControlled: false,
    backgroundColor: Colors.white,
    shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
    builder: (ctx) => const _ChecklistSheet(),
  );
}

class _ChecklistSheet extends StatefulWidget {
  const _ChecklistSheet();
  @override
  State<_ChecklistSheet> createState() => _ChecklistSheetState();
}

class _ChecklistSheetState extends State<_ChecklistSheet> {
  bool a = false, b = false, c = false;
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [
            const Expanded(child: Text('Checklist de estudio', style: TextStyle(fontWeight: FontWeight.w600))),
            IconButton(onPressed: () => Navigator.of(context).pop(), icon: const Icon(Icons.close)),
          ]),
          CheckboxListTile(value: a, onChanged: (v) => setState(() => a = v ?? false), title: const Text('Material listo')), 
          CheckboxListTile(value: b, onChanged: (v) => setState(() => b = v ?? false), title: const Text('Entorno sin distracciones')), 
          CheckboxListTile(value: c, onChanged: (v) => setState(() => c = v ?? false), title: const Text('Objetivo escrito')), 
          const SizedBox(height: 8),
          Align(alignment: Alignment.centerRight, child: FilledButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Cerrar'))),
        ],
      ),
    );
  }
}

