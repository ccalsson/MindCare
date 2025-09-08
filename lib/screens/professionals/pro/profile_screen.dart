import 'package:flutter/material.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: ListView(
        children: [
          const CircleAvatar(radius: 40, child: Icon(Icons.person)),
          const SizedBox(height: 12),
          TextFormField(decoration: const InputDecoration(labelText: 'Nombre completo')),
          const SizedBox(height: 8),
          TextFormField(decoration: const InputDecoration(labelText: 'Bio')),
          const SizedBox(height: 8),
          TextFormField(decoration: const InputDecoration(labelText: 'Especialidades (coma separada)')),
          const SizedBox(height: 8),
          TextFormField(decoration: const InputDecoration(labelText: 'Precio')),
          const SizedBox(height: 16),
          Row(
            children: [
              OutlinedButton(onPressed: () {}, child: const Text('Volver')),
              const SizedBox(width: 8),
              FilledButton(onPressed: () {}, child: const Text('Guardar')),
            ],
          )
        ],
      ),
    );
  }
}

