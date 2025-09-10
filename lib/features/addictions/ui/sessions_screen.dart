import 'package:flutter/material.dart';

class SessionsScreen extends StatelessWidget {
  const SessionsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sesiones')),
      body: Center(
        child: FilledButton(
          onPressed: () {
            // TODO(ccalsson): Integrar con flujo existente de checkout/Stripe.
            // API sugerida: SessionsService.createCheckout(uid, planTier)
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('TODO: iniciar checkout')),
            );
          },
          child: const Text('Reservar sesión'),
        ),
      ),
    );
  }
}

