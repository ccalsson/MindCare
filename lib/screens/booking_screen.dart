import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class BookingScreen extends StatelessWidget {
  final String bookingId;

  const BookingScreen({super.key, required this.bookingId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reserva'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/profesionales'),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('ID de la Reserva: $bookingId'),
            const SizedBox(height: 20),
            const Text('Pantalla de Reserva - En desarrollo'),
          ],
        ),
      ),
    );
  }
}
